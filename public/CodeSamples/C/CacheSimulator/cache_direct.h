/* 
 * File:   cache_direct.h
 * Author: Tom Burger
 *
 * Created on July 30, 2018, 7:33 PM
 */

#ifndef CACHE_DIRECT_H
#define	CACHE_DIRECT_H

#include "storage.h"

void cache_direct_init();
int  cache_direct_load(memory_address addr);
void cache_direct_store(memory_address addr, int value);
void cache_direct_flush();
void cache_direct_stats();

#endif	/* CACHE_DIRECT_H */

