SRC = testcase/*.java \
      com/she/sudoku/generator/*.java \
      com/she/sudoku/model/*.java \
#      com/she/sudoku/ui/*.java \
      com/she/sudoku/util/*.java

#BE CAREFUL when adding items into OBJ list! They will be remove when "make clean"
OBJ = testcase/*.class \
      com/she/sudoku/generator/*.class \
      com/she/sudoku/model/*.class \
#      com/she/sudoku/ui/*.class \
      com/she/sudoku/util/*.class

#BE CAREFUL when adding items into BAK list! They will be remove when "make clean"
#BAK = testcase/*.java~ \
#      com/she/sudoku/generator/*.java~ \
#      com/she/sudoku/model/*.java~ \
#      com/she/sudoku/ui/*.java~ \
#      com/she/sudoku/util/*.java~

all: $(SRC)
	javac $(SRC)
clean:
	rm -f $(OBJ) #$(BAK)
