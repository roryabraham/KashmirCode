//
// Created by Rory Abraham on 12/12/18.
//

#ifndef COMP280_HWK9_CONSUMER_H
#define COMP280_HWK9_CONSUMER_H

#include "Buffer.h"

typedef struct consumer_t consumer_t;

/// returns a consumer type having one of two classes of behavior
/// isPersistent is a flag which denotes the following special behavior if true:
/// consumer can take up to 2 items each opportunity it has.  This consumer does not wait for data availability
    /// If no data is available then it will wait 2 seconds before trying again.
    /// If there is 1 item available it will take that item and then wait 3 seconds before trying again.
    /// If there are 2 items available it will take both items and then wait 4 seconds before trying again
/// if isPersistent is false consumer will take 1 item per second and if none are available will wait until one becomes available
consumer_t * consumer_init(char consumerID, bool isPersistent, cbuf_handle_t cbuf);

/// will tell a producer to start producing dataItems at a rate of 1 item/sleepDuration seconds
void consume(consumer_t * consumer);

#endif //COMP280_HWK9_CONSUMER_H
