/* 
 * File:   cache_associative_full.h
 * Author: Tom Burger
 *
 * Created on July 30, 2018, 7:33 PM
 */

#ifndef CACHE_ASSICIATIVE_FULL_H
#define	CACHE_ASSICIATIVE_FULL_H

#include "storage.h"

void cache_associative_full_init();
int  cache_associative_full_load(memory_address addr);
void cache_associative_full_store(memory_address addr, int value);
void cache_associative_full_flush();
void cache_associative_full_stats();

#endif	/* CACHE_ASSICIATIVE_FULL_H */

