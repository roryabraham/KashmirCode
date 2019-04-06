/*
 * File: cache_direct.c
 * Author: Rory Abraham
 * Author: Ramon Levya
 * Author: Zach Oshana
 *
 * Created: 10/29/18 7:01 PM
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "cache_direct.h"
#include "storage.h"
#include "memory.h"

static int cache_stat_hit = 0;           // for stats
static int cache_stat_miss = 0;          // for stats

#define SET_INDEX_BITS 4

struct Cache_Entry
{
    bool isValid: 1;
    bool isDirty: 1;
    unsigned int tag: 32 - OFFSET_BITS - SET_INDEX_BITS;
    cache_line data;
};

struct Cache_Entry cache[CACHE_ENTRIES];

//Initializes empty cache
void cache_direct_init()
{
    struct Cache_Entry entry;

    // init hit/miss
    cache_stat_hit = 0;
    cache_stat_miss = 0;

    //initialize all cache blocks to 0
    for(int i = 0; i < CACHE_ENTRIES; i++)
    {
        //none are valid to start
        entry.isValid = false;
        entry.isDirty = false;
        entry.tag = 0;
        for(int j = 0; j < CACHE_BLOCK_SIZE; j++)
        {
            entry.data[j] = 0;
        }

        cache[i] = entry;
    }
}

int cache_direct_load(memory_address addr)
{
    bool hit = true;

    //Identify block offset
    unsigned int offset = addr & 0b1111;

    //Identify Set Index
    unsigned int setIndex = (addr >> OFFSET_BITS) & 0b1111;

    //Identify tag
    unsigned int tag = addr >> (OFFSET_BITS + SET_INDEX_BITS);

    //if cache block at addr is invalid OR tag doesn't match
    if(cache[setIndex].isValid == false || tag != cache[setIndex].tag)
    {
        //acknowledge miss
        hit = false;
        cache_stat_miss++;

        // If cache entry is dirty, write to main memory so data is not lost
        if(cache[setIndex].isDirty)
        {
            //Parse address of current cache entry (with offset 0)
            int lookupAddress = ((cache[setIndex].tag << SET_INDEX_BITS) | setIndex) << OFFSET_BITS;

            //write dirty cache entry at address to main storage
            storage_store_line(lookupAddress, cache[setIndex].data);
        }

        // Make a new cache entry
        cache[setIndex].isValid = true;
        cache[setIndex].isDirty = false;
        cache[setIndex].tag = tag;

        // Fetch desired data block from storage
        cache_line desiredData;

        storage_load_line(addr & ~0b1111, desiredData);
        memcpy(&cache[setIndex].data, &desiredData, sizeof(cache_line));
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
                memcpy(&result, &cache[setIndex].data, sizeof(int));
                break;

            // if offset = 4, return bytes 4-7
            case 4:
                memcpy(&result, &cache[setIndex].data[4], sizeof(int));
                break;

            // if offset = 8, return bytes 8-11
            case 8:
                memcpy(&result, &cache[setIndex].data[8], sizeof(int));
                break;

            // if offset = 12, return bytes 12-15
            case 12:
                memcpy(&result, &cache[setIndex].data[12], sizeof(int));
                break;
            default:
                return 0;
        }

        // Count hit
        if(hit)
        {
            ++cache_stat_hit;
        }
 
        return result;
    }
    else
    {
        printf("Error: Invalid offset!");
        return 0;
    }
}


void cache_direct_store(memory_address addr, int value)
{
    //Identify block offset
    unsigned int offset = addr & 0b1111;

    //Identify Set Index
    unsigned int setIndex = (addr >> OFFSET_BITS) & 0b1111;

    //Identify tag
    unsigned int tag = addr >> (OFFSET_BITS + SET_INDEX_BITS);

    // if cache at address is valid and tag matches (hit)
    if(cache[setIndex].isValid == true && cache[setIndex].tag == tag)
    {
        //store the new value in cache
        memcpy(&cache[setIndex].data[offset], &value, sizeof(int));

        // mark cache entry as dirty
        cache[setIndex].isDirty = true;

        //count hit
        cache_stat_hit++;

        return;
    }

    //What if the cache line @ address is not valid?
    else if(cache[setIndex].isValid == false)
    {
        //First, load the cache line @ address from main memory
        cache_line storedLine;

        storage_load_line(addr & ~0b1111, storedLine);
        memcpy(&cache[setIndex].data, &storedLine, sizeof(cache_line));

        //Then, update block with 4 bytes at given offset
        memcpy(&cache[setIndex].data[offset], &value, sizeof(int));

        //mark cache line as valid
        cache[setIndex].isValid = true;

        //mark cache block as dirty
        cache[setIndex].isDirty = true;

        // Count misses
        ++cache_stat_miss;

        return;
    }

    // Or if cache at address is valid but tag doesn't match?
    else if(cache[setIndex].isValid == true && cache[setIndex].tag != tag)
    {
        //Find address of current cache entry (with offset 0)
        int lookupAddress = ((cache[setIndex].tag << SET_INDEX_BITS) | setIndex) << OFFSET_BITS;

        //if the data in the block currently is dirty
        if(cache[setIndex].isDirty)
        {
            //write cache at address to main storage
            storage_store_line(lookupAddress, cache[setIndex].data);
        }

        //bring in cache block at given address (with 0 offset)
        storage_load_line(addr & ~0b1111, cache[setIndex].data);

        cache[setIndex].isValid = true;
        cache[setIndex].tag = tag;

        //create a new cache_line with value contained in the bits ordained by the offset
        memcpy(&cache[setIndex].data[offset], &value, sizeof(int));

        //mark cache entry as dirty
        cache[setIndex].isDirty = true;

        // Count misses
        ++cache_stat_miss;
    }
}

// writes dirty data to memory to make cache and main memory coherent
void cache_direct_flush()
{
    //for each cache entry
    for(int setIndex = 0; setIndex < CACHE_ENTRIES; setIndex++)
    {
        //if cache entry is dirty
        if(cache[setIndex].isDirty)
        {
            //Parse address of current cache entry (with offset 0)
            int lookupAddress = ((cache[setIndex].tag << SET_INDEX_BITS) | setIndex) << OFFSET_BITS;

            //write dirty cache entry at address to main storage
            storage_store_line(lookupAddress, cache[setIndex].data);

            //set cache entry to be clean
            cache[setIndex].isDirty = false;
        }
    }
}

void cache_direct_stats()
{
    printf("cache direct stats:  hit: %d   miss: %d\n", cache_stat_hit, cache_stat_miss);

}
