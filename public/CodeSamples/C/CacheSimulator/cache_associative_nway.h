/* 
 * File:   cache_associative_nway.h
 * Author: Tom Burger
 *
 * Created on July 30, 2018, 7:33 PM
 */

#ifndef CACHE_ASSOCIATIVE_NWAY_H
#define	CACHE_ASSOCIATIVE_NWAY_H

#include "storage.h"

void cache_associative_nway_init();
int  cache_associative_nway_load(memory_address addr);
void cache_associative_nway_store(memory_address addr, int value);
void cache_associative_nway_flush();
void cache_associative_nway_stats();

#endif	/* CACHE_ASSOCIATIVE_NWAY_H */

