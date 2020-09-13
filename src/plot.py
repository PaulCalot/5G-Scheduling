#!/usr/bin/env python3
# -*- coding: utf-8 -*-



#%%



import matplotlib.pyplot as plt
import numpy as np

# File number 
numberOfTextFiles = 3

# ------------ Plot functions ------------------ # 

def analysing(textFile): 
    L= []
    # we suppose it's already sorted.
    textFile.readline() # to remove the first "new channel"
    S = 0
    end = False 
    while(not end):
        l = []
        while(True):
            line=textFile.readline() # 1 space
            if (line == "new channel\n"):
                break
            elif (line == ""):
                end = True
                break
            x = line.split(" ")

            l.append([float(x[0]),float(x[1].split("\n")[0])])
        S+=len(l)
        L.append(np.array(l))
    return (L,S)

def plot(textFile, name): 
    L, S = analysing(textFile)

    plt.xlabel("p")
    plt.ylabel('r')
    plt.title(name + ", remaining triplets, all canals : " + str(S) + ".")

    for k in range(len(L)):
        if (L[k].size >0):  
            plt.plot(L[k][:,0],L[k][:,1],".")
    plt.savefig(name, dpi = 300)
    plt.show()

## ----------- Plot for the selected file ---------------- ##

for k in range(1,numberOfTextFiles+1):
    if (k!=2):
	    nameLP = "test" + str(k) +"LPremoval"
	    nameIP = "test" + str(k) +"IPremoval"
	    nameNaive = "test" + str(k) +"naiveRemoval"
	    nameNoRemoval = "test" + str(k) +"NoRemoval"

	    textFileLP = open(nameLP + ".txt","r")
	    textFileIP = open(nameIP + ".txt","r")
	    textFileNaive = open(nameNaive + ".txt", "r")
	    textFileNoRemoval = open(nameNoRemoval + ".txt", "r")


	    plot(textFileNoRemoval, nameNoRemoval)
	    plot(textFileNaive,nameNaive)
	    plot(textFileIP, nameIP)
	    plot(textFileLP, nameLP)
#%%

