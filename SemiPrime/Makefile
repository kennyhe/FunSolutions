PARAM = -std=c++0x -O2 #-g
all: prime.o  #prime_tasker.o linkedlist.o 
	g++ -o prime *.o -lpthread
prime.o: prime.cpp linkedlist.h prime_tasker.h
	g++ $(PARAM) -c prime.cpp
#prime_tasker.o: prime_tasker.cpp linkedlist.h
#	g++ $(PARAM) -c prime_tasker.cpp
#linkedlist.o: linkedlist.cpp linkedlist.h
#	g++ $(PARAM) -c linkedlist.cpp
clean:
	rm -f prime *.o *.cpp~ *.h~
