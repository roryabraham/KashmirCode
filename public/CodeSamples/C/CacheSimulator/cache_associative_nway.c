/*
 * File: cache_associative_nway.c
 *
 * Author: Rory Abraham
 * Author: Ramon Levya
 * Author: Zach Oshana
 * 
 * Created: October 30th, 2018
 */


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "cache_associative_nway.h"
#include "storage.h"
#include "memory.h"

#define NUM_CACHE_SETS 8
#define SET_INDEX_BITS 3

static int cache_stat_hit = 0;           // for stats
static int cache_stat_miss = 0;          // for stats

struct Cache_Entry
{
    bool isValid: 1;
    bool isDirty: 1;
    unsigned int tag: 32 - OFFSET_BITS - SET_INDEX_BITS;
    cache_line data;
} entry;

struct Cache_Set
{
    struct Cache_Entry entries[2];
    int LRU_Index;  //index of least-recently-used cache entry
} set;

struct Cache_Set cache[NUM_CACHE_SETS];

void cache_associative_nway_init()
{
    cache_stat_hit = 0;
    cache_stat_miss = 0;

    for(int setIndex = 0; setIndex < NUM_CACHE_SETS; setIndex++)
    {
        for(int i = 0; i < 2; i++)
        {
            //initialize entry
            entry.isValid = false;
            entry.isDirty = false;
            entry.tag = 0;
            for(int j = 0; j < CACHE_BLOCK_SIZE; j++)
            {
                entry.data[j] = 0;
            }

            //initialize set
            set.entries[i] = entry;
            set.LRU_Index = 0;
        }

        //initialize cache
        cache[setIndex] = set;
    }
}


int cache_associative_nway_load(memory_address addr)
{
    //Identify block offset
    unsigned int offset = addr & 0b1111;

    //Identify Set Index
    unsigned int setIndex = (addr >> OFFSET_BITS) & 0b111;

    //Identify tag
    unsigned int tag = addr >> (OFFSET_BITS + SET_INDEX_BITS);

    bool hit = false;
    int returnEntryIndex = 0;

    //for each cache entry in the set
    for(int entryIndex = 0; entryIndex < 2; entryIndex++)
    {
        struct Cache_Entry cache_entry = cache[setIndex].entries[entryIndex];

        //if entry is valid and tag match (cache hit)
        if(cache_entry.isValid && cache_entry.tag == tag)
        {
            cache_stat_hit++;
            hit = true;
            returnEntryIndex = entryIndex;

            //update LRU_index of set
            cache[setIndex].LRU_Index = (returnEntryIndex == 0) ? 1 : 0;

            break;
        }
    }

    if(!hit)
    {
        //count miss
        cache_stat_miss++;

        //identify the cache entry to be replaced
        struct Cache_Entry LRU_entry = cache[setIndex].entries[cache[setIndex].LRU_Index];
        returnEntryIndex = cache[setIndex].LRU_Index;

        //if data is dirty write to main memory
        if(LRU_entry.isDirty)
        {
            int lookupAddress = ((LRU_entry.tag << SET_INDEX_BITS) | setIndex) << OFFSET_BITS;

            //write dirty cache entry at address to main storage
            storage_store_line(lookupAddress, LRU_entry.data);
        }

        //update fields to make new cache entry
        cache[setIndex].entries[cache[setIndex].LRU_Index].isValid = true;
        cache[setIndex].entries[cache[setIndex].LRU_Index].isDirty = false;
        cache[setIndex].entries[cache[setIndex].LRU_Index].tag = tag;

        // Fetch desired data block from storage
        cache_line desiredData;
        storage_load_line(addr & ~0b1111, desiredData);
        memcpy(&cache[setIndex].entries[returnEntryIndex].data, &desiredData, sizeof(cache_line));

        //Update the LRU index of the set
        cache[setIndex].LRU_Index = (returnEntryIndex == 0) ? 1 : 0;
    }

    int result = 0;

    //Assert that the offset is divisible by 4
    if(offset % 4 == 0)
    {
        // cache block is 16 bytes. We need to return a 4 byte value, depending on the offset.
        switch(offset)
        {
            // if offset = 0, return bytes 0-3
            case 0:
                memcpy(&result, &cache[setIndex].entries[returnEntryIndex].data, sizeof(int));
                break;

            // if offset = 4, return bytes 4-7
            case 4:
                memcpy(&result, &cache[setIndex].entries[returnEntryIndex].data[4], sizeof(int));
                break;

            // if offset = 8, return bytes 8-11
            case 8:
                memcpy(&result, &cache[setIndex].entries[returnEntryIndex].data[8], sizeof(int));
                break;

            // if offset = 12, return bytes 12-15
            case 12:
                memcpy(&result, &cache[setIndex].entries[returnEntryIndex].data[12], sizeof(int));
                break;
            default:
                return 0;
        }

        return result;
    }
    else
    {
        printf("Error: Invalid offset!");
        return 0;
    }

}

void cache_associative_nway_store(memory_address addr, int value)
{
    //Identify block offset
    unsigned int offset = addr & 0b1111;

    //Identify Set Index
    unsigned int setIndex = (addr >> OFFSET_BITS) & 0b111;

    //Identify tag
    unsigned int tag = addr >> (OFFSET_BITS + SET_INDEX_BITS);

    bool compulsoryMiss = false;
    int targetEntryIndex = 0;

    //Check first for hits
    for(int entryIndex = 0; entryIndex < 2; entryIndex++)
    {
        struct Cache_Entry cache_entry = cache[setIndex].entries[entryIndex];

        // if entry is valid and tag matches (hit)
        if(cache_entry.isValid && cache_entry.tag == tag)
        {
            cache_stat_hit++;

            //insert value into target cache block, update tag, and mark as both valid and dirty
            memcpy(&cache[setIndex].entries[entryIndex].data[offset], &value, sizeof(int));
            cache[setIndex].entries[entryIndex].tag = tag;
            cache[setIndex].entries[entryIndex].isValid = true;
            cache[setIndex].entries[entryIndex].isDirty = true;

            //Update the LRU index of the set
            cache[setIndex].LRU_Index = (entryIndex == 0) ? 1 : 0;

            return;
        }
    }

    // Then check for compulsory misses
    for(int entryIndex = 0; entryIndex < 2; entryIndex++)
    {
        //if entry is invalid (compulsory miss)
        if(!cache[setIndex].entries[entryIndex].isValid)
        {
            targetEntryIndex = entryIndex;
            cache_stat_miss++;
            compulsoryMiss = true;

            //fetch desired data block from storage (for simulation purposes...
                // what if there were stored data at that address?)
            cache_line desiredData;
            storage_load_line(addr & ~0b1111, desiredData);
            memcpy(&cache[setIndex].entries[entryIndex].data,&desiredData,sizeof(cache_line));

            break;
        }
    }

    // finally check for capacity miss or conflict miss
    if(!compulsoryMiss)
    {
        //count miss
        cache_stat_miss++;

        //identify cache entry to be booted
        struct Cache_Entry LRU_entry = cache[setIndex].entries[cache[setIndex].LRU_Index];
        targetEntryIndex = cache[setIndex].LRU_Index;

        //if data is dirty write to main storage
        if(LRU_entry.isDirty)
        {
            int lookupAddress = ((LRU_entry.tag << SET_INDEX_BITS) | setIndex) << OFFSET_BITS;

            //write dirty cache entry at address to main storage
            storage_store_line(lookupAddress, LRU_entry.data);
        }

        // Fetch desired data block from storage
        cache_line desiredData;
        storage_load_line(addr & ~0b1111, desiredData);
        memcpy(&cache[setIndex].entries[cache[setIndex].LRU_Index].data, &desiredData, sizeof(cache_line));

        //Update the LRU index of the set
        //cache[setIndex].LRU_Index = (cache[setIndex].LRU_Index == 0) ? 1 : 0;
    }

    //The following steps should occur only if there was a cache miss:
    //insert value into target cache block, update tag, and mark as both valid and dirty
    memcpy(&cache[setIndex].entries[targetEntryIndex].data[offset], &value, sizeof(int));
    cache[setIndex].entries[targetEntryIndex].tag = tag;
    cache[setIndex].entries[targetEntryIndex].isValid = true;
    cache[setIndex].entries[targetEntryIndex].isDirty = true;

    //Update the LRU index of the set
    cache[setIndex].LRU_Index = (targetEntryIndex == 0) ? 1 : 0;
}

void cache_associative_nway_flush()
{
    //for each set in the cache
    for(int setIndex = 0; setIndex < NUM_CACHE_SETS; setIndex++)
    {
        //for each line in the set
        for(int entryIndex = 0; entryIndex < 2; entryIndex++)
        {
            // if entry is dirty
            if(cache[setIndex].entries[entryIndex].isDirty)
            {
                //write dirty cache entry at address to main storage
                int lookupAddress = ((cache[setIndex].entries[entryIndex].tag << SET_INDEX_BITS) | setIndex) << OFFSET_BITS;
                storage_store_line(lookupAddress, cache[setIndex].entries[entryIndex].data);

                //mark entry as clean
                cache[setIndex].entries[entryIndex].isDirty = false;
            }
        }
    }
}

void cache_associative_nway_stats()
{
    printf("cache 2-way stats:  hits: %d   misses: %d\n", cache_stat_hit, cache_stat_miss);
}

