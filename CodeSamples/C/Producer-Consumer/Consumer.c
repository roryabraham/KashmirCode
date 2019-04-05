//
// Created by Rory Abraham on 12/12/18.
//

#include <time.h>
#include "Consumer.h"
#include "Buffer.h"

/// consumer type can be one of two types of objects, depending on boolean flag isPersistent
/// see Consumer.h for difference in functionality
struct consumer_t{
    char consumerID;
    bool isPersisent;
    cbuf_handle_t cbuf;
};

consumer_t * consumer_init(char consumerID, bool isPersistent, cbuf_handle_t cbuf)
{
    assert(consumerID && cbuf);
    consumer_t * consumer = malloc(sizeof(consumer_t));

    consumer->consumerID = consumerID;
    consumer->isPersisent = isPersistent;
    consumer->cbuf = cbuf;

    return consumer;
}

/// Helper method to print out a data line
static void printDataItem(consumer_t * consumer, DataItem * dataItem)
{
    // convert uuid from binary to string
    char str[37];
    uuid_unparse(dataItem->serialNum, str);

    // get current time
    time_t now;
    time(&now);

    // print info
    printf("ConsumerID: %c, SerialNum: %s, ProducerID: %c, Time: %s\n",
           consumer->consumerID, str, dataItem->producerID, ctime(&now));
}

void consume(consumer_t * consumer)
{
    if(consumer->isPersisent)
    {
        while(true)
        {
            // attempt to lock the mutex
            if(cbuf_trylock(consumer->cbuf) != 0)
            {
                // if mutex unavailable, sleep 2 seconds then continue
                sleep(2);
                continue;
            }

            // check if buffer is empty
            if(cbuf_isEmpty(consumer->cbuf))
            {
                // release mutex and wait
                cbuf_unlock(consumer->cbuf);
                sleep(2);
                continue;
            }

            // if there are two or more items available for consumption
            if(cbuf_currentSize(consumer->cbuf) >= 2)
            {
                // take both data items
                DataItem * item1 = malloc(sizeof(DataItem));
                DataItem * item2 = malloc(sizeof(DataItem));
                cbuf_get(consumer->cbuf, item1);
                cbuf_get(consumer->cbuf, item2);

                //consume both items
                printDataItem(consumer, item1);
                printDataItem(consumer, item2);

                //release mutex
                cbuf_unlock(consumer->cbuf);

                // sleep for 4 seconds
                sleep(4);
                continue;
            }
            else    // if there is only one item available for consumption
            {
                // get the item
                DataItem * dataItem = malloc(sizeof(DataItem));
                cbuf_get(consumer->cbuf, dataItem);

                // consume the item
                printDataItem(consumer, dataItem);

                // release mutex
                cbuf_unlock(consumer->cbuf);

                // sleep for 3 seconds
                sleep(3);
                continue;
            }
        }
    }
    else
    {
        while(true)
        {
            // lock mutex, wait if unavailable
            cbuf_lock(consumer->cbuf);

            // get data if buffer is nonempty
            DataItem * dataItem = malloc(sizeof(DataItem));
            if(cbuf_get(consumer->cbuf, dataItem) == 0)
            {
                // consume data
                printDataItem(consumer, dataItem);
            }

            // unlock mutex
            cbuf_unlock(consumer->cbuf);

            // sleep for 1 second
            sleep(1);
        }
    }
}
