import random

"""
An implementation of the Gale-Shapely solution to the stable matching problem
File: stablematchin.py
Author: Rory Abraham
Date: 1/31/19

"""
# A dictionary mapping men to their list of preferences
men = {
    'Jim': ['Jane', 'Margaret', 'Jacki', 'Annabelle', 'Sakura', 'Taylor', 'Gabriella', 'Zoey'],
    'Dave': ['Jacki', 'Jane', 'Annabelle', 'Taylor', 'Gabriella', 'Zoey', 'Sakura', 'Margaret'],
    'Patrick': ['Annabelle', 'Jacki', 'Margaret', 'Jane', 'Taylor', 'Sakura', 'Zoey', 'Gabriella'],
    'Michael': ['Jacki', 'Taylor', 'Margaret', 'Annabelle', 'Gabriella', 'Zoey', 'Jane', 'Sakura'],
    'John': ['Gabriella', 'Margaret', 'Zoey', 'Sakura', 'Jane', 'Annabelle', 'Jacki', 'Taylor'],
    'Bryce': ['Margaret', 'Zoey', 'Sakura', 'Gabriella', 'Taylor', 'Annabelle', 'Jane', 'Jacki'],
    'Connor': ['Jacki', 'Jane', 'Annabelle', 'Taylor', 'Zoey', 'Gabriella', 'Sakura', 'Margaret'],
    'Tyler': ['Jane', 'Jacki', 'Annabelle', 'Zoey', 'Sakura', 'Taylor', 'Gabriella', 'Margaret']
}

# A dictionary mapping women to their list of preferences
women = {
    'Jacki': ['Michael', 'Tyler', 'Jim', 'Patrick', 'Connor', 'Bryce', 'John', 'Dave'],
    'Jane': ['Patrick', 'Bryce', 'John', 'Jim', 'Dave', 'Michael', 'Connor', 'Tyler'],
    'Annabelle': ['Michael', 'Dave', 'Jim', 'John', 'Tyler', 'Connor', 'Bryce'],
    'Taylor': ['Tyler', 'Connor', 'Bryce', 'John', 'Patrick', 'Michael', 'Dave', 'Jim'],
    'Margaret': ['Bryce', 'Patrick', 'Dave', 'Jim', 'John', 'Connor', 'Tyler', 'Michael'],
    'Gabriella': ['Michael', 'Dave', 'Jim', 'Connor', 'Bryce', 'Tyler', 'John', 'Patrick'],
    'Sakura': ['Connor', 'John', 'Jim', 'Bryce', 'Michael', 'Patrick', 'Dave', 'Tyler'],
    'Zoey': ['Jim', 'Patrick', 'Dave', 'Michael', 'Connor', 'Tyler', 'John', 'Bryce']
}

# Initially no proposals have occurred
proposals = {
    'Jim': [],
    'Dave': [],
    'Patrick': [],
    'Michael': [],
    'John': [],
    'Bryce': [],
    'Connor': [],
    'Tyler': []
}

# Initially all men and women are single
singleMen = ['Jim', 'Dave', 'Patrick', 'Michael', 'John', 'Bryce', 'Connor', 'Tyler']
singleWomen = ['Jacki', 'Jane', 'Annabelle', 'Taylor', 'Margaret', 'Gabriella', 'Sakura', 'Zoey']

pairs = []


def courting():
    bachelor = random.choice(singleMen)
    if proposals[bachelor].__len__() == 8:
        print('Bachelor has proposed to everyone. Error!')
        return
    preferences = men[bachelor]
    # loop through the list of preferences and find first one which he has not proposed to
    for bachelorette in preferences:
        if bachelorette in proposals[bachelor]:
            continue
        proposals[bachelor].append(bachelorette)
        if bachelorette in singleWomen:
            # bachelor and bachelorette become engaged
            pairs.append((bachelor, bachelorette))
            singleMen.remove(bachelor)
            singleWomen.remove(bachelorette)
            return
        else:
            # find the woman's fiance
            for pair in pairs:
                if bachelorette in pair:
                    rival = pair[0]
                    break
            for herPreference in women[bachelorette]:
                if herPreference == rival:
                    return
                if herPreference == bachelor:
                    for couple in pairs:
                        # previous pairing of bachelor and rival removed
                        if bachelorette in couple:
                            pairs.remove(couple)
                            # bachelor and bachelorette become engaged
                            pairs.append((bachelor, bachelorette))
                            singleMen.remove(bachelor)
                            singleMen.append(rival)
                            return


while singleMen.__len__() != 0:
    courting()

print(pairs)
