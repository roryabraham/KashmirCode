//
// Created by Rory Abraham on 12/12/18.
//

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <pthread.h>
#include <memory.h>
#include <uuid/uuid.h>
#include <unistd.h>

#ifndef COMP280_HWK9_BUFFER_H
#define COMP280_HWK9_BUFFER_H

// type declaration for our buffer struct
typedef struct circular_buf_t circular_buf_t;

// type declaration for pointer to buffer
typedef circular_buf_t* cbuf_handle_t;

// type declaration for a single data item
typedef struct DataItem DataItem;

struct DataItem {
    uuid_t * serialNum;
    char producerID;
};

/// constructor for a DataItem
DataItem * dataItem_init(uuid_t * serialNum, char producerID);

/// pass a storage buffer and size to constructor
cbuf_handle_t cbuf_init(DataItem * buffer, unsigned size);

/// free a cbuf
void cbuf_free(cbuf_handle_t cbuf);

/// reset a cbuf
void cbuf_reset(cbuf_handle_t cbuf);

/// add to the buffer, reject new data if buffer is full
/// returns 0 on success, -1 if buffer is full
int cbuf_put(cbuf_handle_t cbuf, DataItem * data);

/// Retrieve a value from the buffer
/// return 0 on success, -1 if buffer is empty
int cbuf_get(cbuf_handle_t cbuf, DataItem * data);

/// returns true if cbuf is full
bool cbuf_isFull(cbuf_handle_t cbuf);

/// returns true if cbuf is empty
bool cbuf_isEmpty(cbuf_handle_t cbuf);

/// returns the max capacity of buffer
unsigned cbuf_maxCapacity(cbuf_handle_t cbuf);

/// returns the number of elements currently in the buffer
unsigned cbuf_currentSize(cbuf_handle_t cbuf);

/// locks the mutex of the buffer
/// returns 0 if lock successful, else -1, calling thread blocked until mutex available
int cbuf_lock(cbuf_handle_t cbuf);

/// locks the mutex of the buffer
/// returns 0 if lock successful, -1 if mutex is already locked by another thread
int cbuf_trylock(cbuf_handle_t cbuf);

/// unlocks the mutex of the buffer
/// returns 0 if unlock successful, else returns -1
int cbuf_unlock(cbuf_handle_t cbuf);

#endif //COMP280_HWK9_BUFFER_H
