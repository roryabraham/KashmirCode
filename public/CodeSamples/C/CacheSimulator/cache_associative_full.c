/*
 * File: cache_associative_full.c
 *
 * Author: Rory Abraham
 * Author: Ramon Levya
 * Author: Zach Oshana
 *
 * Created: 10/30/18
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "cache_associative_full.h"
#include "storage.h"
#include "memory.h"
#include "queue.h"

static int cache_stat_hit = 0;           // for stats
static int cache_stat_miss = 0;          // for stats

struct Cache_Entry
{
    bool isValid: 1;
    bool isDirty: 1;
    unsigned int tag: 32 - OFFSET_BITS;
    cache_line data;
} entry;

struct Cache_Entry cache[CACHE_ENTRIES];

/*
 * A Global array of nodes, so that memory for all the nodes are allocated beforehand
 * and any given node can be accessed by its index
 */
static struct Node nodeDictionary[CACHE_ENTRIES];
static struct Queue queue;

//initializes empty cache
void cache_associative_full_init()
{
    // init hit/miss
    cache_stat_hit = 0;
    cache_stat_miss = 0;

    //initialize LRU queue
    queue.nodeCount = 0;
    queue.front = NULL;
    queue.rear = NULL;
    queue.size = CACHE_ENTRIES;

    //initialize all cache blocks to 0
    for(unsigned int i = 0; i < CACHE_ENTRIES; i++)
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

        //initialize nodeDictionary
        nodeDictionary[i].index = i;
        nodeDictionary[i].prev = NULL;
        nodeDictionary[i].next = NULL;

        //fill LRU queue with nodes from the dictionary
        enqueue(&queue, &nodeDictionary[i]);
    }
}


int cache_associative_full_load(memory_address addr)
{
    //Identify block offset
    unsigned int offset = addr & 0b1111;

    //NO SET INDEX

    //Identify tag
    unsigned int tag = addr >> (OFFSET_BITS);

    bool hit = false;
    int returnEntryIndex = 0;

    //Check for hits
    int cacheIndex = 0;     //incrementer
    while(!hit && cacheIndex < CACHE_ENTRIES)
    {
        //if cache entry is valid and tag matches (hit)
        if(cache[cacheIndex].isValid && cache[cacheIndex].tag == tag)
        {
            returnEntryIndex = cacheIndex;
            cache_stat_hit++;
            hit = true;

            //enqueue node pointing to returnEntryIndex
            enqueue(&queue,&nodeDictionary[returnEntryIndex]);
        }

        cacheIndex++;
    }

    if(!hit)
    {
        //count miss
        cache_stat_miss++;

        //identify the cache entry to be replaced
        if(queue.nodeCount == 0)
        {
            returnEntryIndex = 0;
        }
        else
        {
            returnEntryIndex = dequeue(&queue)->index;
        }

        struct Cache_Entry LRU_entry = cache[returnEntryIndex];

        //if data is dirty write to main memory
        if(LRU_entry.isDirty)
        {
            int lookupAddress = LRU_entry.tag  << OFFSET_BITS;

            //write dirty cache entry at address to main storage
            storage_store_line(lookupAddress, LRU_entry.data);
        }

        //update fields to make a new cache entry
        cache[returnEntryIndex].isValid = true;
        cache[returnEntryIndex].isDirty = false;
        cache[returnEntryIndex].tag = tag;

        //fetch desired data block from storage
        cache_line desiredData;
        storage_load_line(addr & ~0b1111, desiredData);
        memcpy(&cache[returnEntryIndex].data, &desiredData, sizeof(cache_line));

        //enqueue node pointing to returnEntryIndex
        enqueue(&queue,&nodeDictionary[returnEntryIndex]);
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
                memcpy(&result, &cache[returnEntryIndex].data, sizeof(int));
                break;

                // if offset = 4, return bytes 4-7
            case 4:
                memcpy(&result, &cache[returnEntryIndex].data[4], sizeof(int));
                break;

                // if offset = 8, return bytes 8-11
            case 8:
                memcpy(&result, &cache[returnEntryIndex].data[8], sizeof(int));
                break;

                // if offset = 12, return bytes 12-15
            case 12:
                memcpy(&result, &cache[returnEntryIndex].data[12], sizeof(int));
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


void cache_associative_full_store(memory_address addr, int value)
{
    //Identify block offset
    unsigned int offset = addr & 0b1111;

    //NO SET INDEX

    //Identify tag
    unsigned int tag = addr >> OFFSET_BITS;

    int targetEntryIndex = 0;
    bool targetFound = false;

    //search through the entire cache for a valid matching tag (hit)
    for(int i = 0; i < CACHE_ENTRIES; i++)
    {
        if(cache[i].tag == tag && cache[i].isValid)
        {
            targetEntryIndex = i;
            cache_stat_hit++;
            targetFound = true;
            break;
        }
    }

    //search through the entire cache for open (invalid) spaces (compulsory miss)
    if(!targetFound)
    {
        for (int i = 0; i < CACHE_ENTRIES; i++)
        {
            if (!cache[i].isValid)
            {
                cache_stat_miss++;
                targetEntryIndex = i;
                targetFound = true;

                //fetch desired data block from storage (for simulation purposes...
                        // what if there were stored data at that address?)
                cache_line desiredData;
                storage_load_line(addr & ~0b1111, desiredData);
                memcpy(&cache[targetEntryIndex].data, &desiredData, sizeof(cache_line));

                break;
            }
        }
    }

    //if cache is full and desired entry is not found
    if(!targetFound)
    {
        cache_stat_miss++;

        //choose an entry to boot
        targetEntryIndex = dequeue(&queue)->index;
        struct Cache_Entry LRU_entry = cache[targetEntryIndex];

        //if data is dirty write to main memory
        if(LRU_entry.isDirty)
        {
            int lookupAddress = LRU_entry.tag  << OFFSET_BITS;

            //write dirty cache entry at address to main storage
            storage_store_line(lookupAddress, LRU_entry.data);
        }

        //update fields to make a new cache entry
        cache[targetEntryIndex].isValid = true;
        cache[targetEntryIndex].isDirty = false;
        cache[targetEntryIndex].tag = tag;

        //fetch desired data block from storage
        cache_line desiredData;
        storage_load_line(addr & ~0b1111, desiredData);
        memcpy(&cache[targetEntryIndex].data, &desiredData, sizeof(cache_line));
    }

    //insert value into target cache block and mark as dirty
    memcpy(&cache[targetEntryIndex].data[offset], &value, sizeof(int));
    cache[targetEntryIndex].isValid = true;
    cache[targetEntryIndex].isDirty = true;
    cache[targetEntryIndex].tag = tag;

    //enqueue node pointing to targetEntryIndex
    enqueue(&queue,&nodeDictionary[targetEntryIndex]);
}

void cache_associative_full_flush()
{
    //for each cache entry
    for(int i = 0; i < CACHE_ENTRIES; i++)
    {
        if(cache[i].isDirty)
        {
            //write dirty cache entry at address to main storage
            int lookupAddress = cache[i].tag  << OFFSET_BITS;
            storage_store_line(lookupAddress, cache[i].data);

            //mark cache entry as clean
            cache[i].isDirty = false;
        }
    }
}

void cache_associative_full_stats()
{
    printf("cache fully associative stats:  hits: %d   misses: %d\n", cache_stat_hit, cache_stat_miss);
}