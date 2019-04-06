//
// Created by Rory Abraham on 12/12/18.
//


#include "Buffer.h"
#include "Producer.h"

struct producer_t{
    char producerID;
    unsigned sleepDuration;
    cbuf_handle_t cbuf;
};

producer_t * producer_init(char producerID, unsigned sleepDuration, cbuf_handle_t cbuf)
{
    assert(producerID && sleepDuration && sleepDuration != 0 && cbuf);

    // allocate memory for producer object
    struct producer_t * producer = malloc(sizeof(producer_t));

    producer->producerID = producerID;
    producer->sleepDuration = sleepDuration;
    producer->cbuf = cbuf;

    return producer;
}

void produce(producer_t * producer)
{
    while(true)
    {
        // lock mutex (wait if unavailable)
        cbuf_lock(producer->cbuf);

        // generate uuid
        uuid_t uuid;
        uuid_generate(uuid);

        // generate dataItem
        DataItem * dataItem = dataItem_init(&uuid, producer->producerID);

        // attempt to add to buffer
        cbuf_put(producer->cbuf, dataItem);

        // unlock mutex
        cbuf_unlock(producer->cbuf);

        // sleep for a set amount of time
        sleep(producer->sleepDuration);
    }
}