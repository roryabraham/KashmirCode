#include "memory.h"
#include <stdio.h>
#include "storage.h"
#include "cache_direct.h"
#include "cache_associative_full.h"
#include "cache_associative_nway.h"

static int mem_stat_load = 0;
static int mem_stat_store = 0;
static enum cache_type cache = NO_CACHE;

void memory_init(enum cache_type ct)
{
    mem_stat_load = 0;
    mem_stat_store = 0;
    storage_init();
    cache = ct;
    switch (cache)
    {
        case NO_CACHE:
            printf("No Cache\n");
            break;

        case CACHE_DIRECT:
            printf("Direct Cache\n");
            cache_direct_init();
            break;

        case CACHE_ASSOCIATIVE_FULL:
            printf("Fully Associative Cache\n");
            cache_associative_full_init();
            break;

        case CACHE_ASSOCIATIVE_NWAY:
            printf("NWay Cache\n");
            cache_associative_nway_init();
            break;

        default:
            printf("invalid cache type %d\n", cache);
            break;
    }
}

int  memory_load(memory_address addr)
{
    int result;
    ++mem_stat_load;
    switch (cache)
    {
        case NO_CACHE:
            result = storage_load(addr);
            break;
            
        case CACHE_DIRECT:
            result = cache_direct_load(addr);
            break;
            
        case CACHE_ASSOCIATIVE_FULL:
            result = cache_associative_full_load(addr);
            break;
            
        case CACHE_ASSOCIATIVE_NWAY:
            result = cache_associative_nway_load(addr);
            break;

        default:
            printf("invalid cache type %d\n", cache);
            result = 0;
            break;
    }
    return result;
}

void memory_store(memory_address addr, int value)
{
    ++mem_stat_store;
    switch (cache)
    {
        case NO_CACHE:
            storage_store(addr, value);
            break;

        case CACHE_DIRECT:
            cache_direct_store(addr, value);
            break;
            
        case CACHE_ASSOCIATIVE_FULL:
            cache_associative_full_store(addr, value);
            break;
            
        case CACHE_ASSOCIATIVE_NWAY:
            cache_associative_nway_store(addr, value);
            break;

        default:
            printf("invalid cache type %d\n", cache);
            break;
    }
}

void memory_flush()
{
    switch (cache)
    {
        case NO_CACHE:
            break;

        case CACHE_DIRECT:
            cache_direct_flush();
            break;

        case CACHE_ASSOCIATIVE_FULL:
            cache_associative_full_flush();
            break;

        case CACHE_ASSOCIATIVE_NWAY:
            cache_associative_nway_flush();
            break;

        default:
            printf("invalid cache type %d\n", cache);
            break;
    }
    
}

void memory_stats()
{
    printf("memory statistics:  loads: %d   stores: %d\n", mem_stat_load, mem_stat_store);
    storage_stats();

    switch (cache)
    {
        case NO_CACHE:
            break;

        case CACHE_DIRECT:
            cache_direct_stats();
            break;

        case CACHE_ASSOCIATIVE_FULL:
            cache_associative_full_stats();
            break;

        case CACHE_ASSOCIATIVE_NWAY:
            cache_associative_nway_stats();
            break;

        default:
            printf("invalid cache type %d\n", cache);
            break;
    }
}
