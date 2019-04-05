//
// Created by Rory Abraham on 12/12/18.
//


#include "Buffer.h"

// TODO: make buffer thread-safe

struct circular_buf_t {
    DataItem * buffer;
    unsigned head;
    unsigned tail;
    unsigned maxSize;
    bool isFull;
    pthread_mutex_t lock;
};

static void advancePointer(cbuf_handle_t cbuf);

/// ALL METHODS USE ASSERTS RATHER THAN CONDITIONALS

DataItem * dataItem_init(uuid_t * serialNum, char producerID)
{
    DataItem * dataItem = malloc(sizeof(DataItem));
    dataItem->producerID = producerID;
    dataItem->serialNum = serialNum;
    assert(dataItem);
    return dataItem;
}

cbuf_handle_t cbuf_init(DataItem * buffer, unsigned size)
{
    assert(buffer && size);

    // allocate dynamic memory in the heap for the cbuf
    cbuf_handle_t cbuf = malloc(sizeof(circular_buf_t));
    assert(cbuf);

    // set all fields
    cbuf->buffer = buffer;
    cbuf->maxSize = size;
    cbuf_reset(cbuf);

    // make sure buffer is initialized as empty
    assert(cbuf_isEmpty(cbuf));

    // initialize mutex
    if(pthread_mutex_init(&cbuf->lock, NULL) != 0)
    {
        printf("Mutex initialization failed");
        exit(-1);
    }

    return cbuf;
}

// just empty the buffer
void cbuf_reset(cbuf_handle_t cbuf)
{
    assert(cbuf);

    cbuf->head = 0;
    cbuf->tail = 0;
    cbuf->isFull = false;
}

void cbuf_free(cbuf_handle_t cbuf)
{
    assert(cbuf);
    free(cbuf);
}

bool cbuf_isFull(cbuf_handle_t cbuf)
{
    assert(cbuf);
    return cbuf->isFull;
}

bool cbuf_isEmpty(cbuf_handle_t cbuf)
{
    assert(cbuf);
    return(!cbuf->isFull && (cbuf->head == cbuf->tail));
}

unsigned cbuf_maxCapacity(cbuf_handle_t cbuf)
{
    assert(cbuf);
    return cbuf->maxSize;
}

unsigned cbuf_currentSize(cbuf_handle_t cbuf)
{
    assert(cbuf);

    // assume the buffer is full
    unsigned size = cbuf->maxSize;

    // if the buffer is not full
    if(!cbuf->isFull)
    {
        // if head is in front of tail
        if(cbuf->head >= cbuf->tail)
        {
            // return the difference between the two
            size = (cbuf->head - cbuf->tail);
        }
        else // if tail is in front of head
        {
            // return the difference between the two offset by the max size
            size = (cbuf->maxSize + cbuf->head - cbuf->tail);
        }
    }

    return size;
}

// static helper methods for pointer logic
static void advancePointer(cbuf_handle_t cbuf)
{
    assert(cbuf);

    if(cbuf->isFull)
    {
        // modulo will cause tail to be reset to zero when it reaches maxSize
        cbuf->tail = (cbuf->tail + 1) % cbuf->maxSize;
    }

    cbuf->head = (cbuf->head + 1) % cbuf->maxSize;
    cbuf->isFull = (cbuf->head == cbuf->tail);
}

// static helper method for pointer logic
static void retreatPointer(cbuf_handle_t cbuf)
{
    assert(cbuf);

    cbuf->isFull = false;
    cbuf->tail = (cbuf->tail + 1) % cbuf->maxSize;
}

// put blocks if buffer is full
int cbuf_put(cbuf_handle_t cbuf, DataItem * data)
{
    int ret = -1;

    assert(cbuf && cbuf->buffer);

    // if buffer is not full
    if(!cbuf_isFull(cbuf))
    {
        // insert data at head and advance head pointer
        //memcpy(cbuf->buffer[cbuf->head], data, sizeof(DataItem));
        cbuf->buffer[cbuf->head] = *data;
        advancePointer(cbuf);
        ret = 0;
    }

    return ret;
}

int cbuf_get(cbuf_handle_t cbuf, DataItem * data)
{
    int ret = -1;

    assert(cbuf && cbuf->buffer);

    // if buffer is not empty
    if(!cbuf_isEmpty(cbuf))
    {
        // load data at tail into data pointer and retreat tail pointer
        *data = cbuf->buffer[cbuf->tail];
        retreatPointer(cbuf);
        ret = 0;
    }

    return ret;
}

int cbuf_lock(cbuf_handle_t cbuf)
{
    int ret = 0;

    if(pthread_mutex_lock(&cbuf->lock) != 0)
        ret = -1;

    return ret;
}

int cbuf_trylock(cbuf_handle_t cbuf)
{
    return pthread_mutex_trylock(&cbuf->lock);
}

int cbuf_unlock(cbuf_handle_t cbuf)
{
    int ret = 0;

    if(pthread_mutex_unlock(&cbuf->lock) != 0)
        ret = -1;

    return ret;
}