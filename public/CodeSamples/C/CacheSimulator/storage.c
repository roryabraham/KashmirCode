#include "storage.h"
#include <stdio.h>
#include <string.h>

static int storage_stat_load_cost = 0;
static int storage_stat_store_cost = 0;


#define START_COST 50
#define INCREMENTAL_COST 10

static unsigned char storage[MAX_STORAGE_SIZE];
void storage_init()
{
    storage_stat_load_cost = 0;
    storage_stat_store_cost = 0;
}

int  storage_load(memory_address addr)
{
    if (addr > MAX_STORAGE_SIZE - sizeof(int))
    {
        printf("invalid memory address %08x", addr);
        return -1;
    }
    storage_stat_load_cost += START_COST + INCREMENTAL_COST * sizeof(int);
    int result;
    memcpy(&result, &storage[addr], sizeof(int));
    return result;
}

void storage_store(memory_address addr, int value)
{
    if (addr > MAX_STORAGE_SIZE - sizeof(int))
    {
        printf("invalid memory address %08x", addr);
        return;
    }
    storage_stat_store_cost += START_COST + INCREMENTAL_COST * sizeof(int);
    memcpy(&storage[addr], &value, sizeof(int));
}

void storage_load_line(memory_address addr, cache_line cl)
{
    if (addr > MAX_STORAGE_SIZE - sizeof(cache_line))
    {
        printf("invalid memory address %08x", addr);
        return;
    }
    storage_stat_load_cost += START_COST + INCREMENTAL_COST * sizeof(cache_line);
    memcpy(cl, &storage[addr], sizeof(cache_line));
}
void storage_store_line(memory_address addr, cache_line cl)
{
    if (addr > MAX_STORAGE_SIZE - sizeof(cache_line))
    {
        printf("invalid memory address %08x", addr);
        return;
    }
    storage_stat_store_cost += START_COST + INCREMENTAL_COST * sizeof(cache_line);
    memcpy(&storage[addr], cl, sizeof(cache_line));
    
}

void storage_stats()
{
    printf("storage statistics:  load cost: %d   store cost: %d\n", storage_stat_load_cost, storage_stat_store_cost);
}

