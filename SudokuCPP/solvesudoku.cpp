#include <stdlib.h>
#include <iostream>
#include <vector>
#include <sys/time.h>
#include "set.h"

//#define DEBUG
#ifndef START_TRY
    #define START_TRY 5
#endif

#ifndef MAX_DEPTH
    #define MAX_DEPTH 40
#endif

#ifndef TRY_COUNT
    #define TRY_COUNT 100
#endif

using namespace std;

class Sudoku {

private:
    size_t d[9][9];
    size_t q[9][9];
    bool generated;
    
    set row_free_index[9]; // The index and value can be put in row/column/small square
    set row_free_value[9];
    set col_free_index[9];
    set col_free_value[9];
    set squ_free_index[9];
    set squ_free_value[9];
    set allow[9][9]; // The numbers allow to put into current cell
    static const size_t values[];
public:
    Sudoku():generated(false) {};
    void solveSudoku(vector<vector<char> > &board);
    void solveSudoku(size_t q[][9]);
    void genSudoku();
    void getQuestion(size_t r[][9]) { if (! generated) genSudoku(); generated = true; copyArray(q, r); }
    
private:
    void preSet();
    bool init(vector<vector<char> > &board);
    void init(const size_t q[][9]) {copyArray(q, d); preSet();}
    int solve(bool solve = true, bool needInfo = true); // Return 1: solved; 0: Need more known numbers; -1: Error in data.
    void fill(const size_t x, const size_t y, const size_t v, bool solve = true);
    bool canFill(const size_t x, const size_t y, size_t& v);
    int goDFS(size_t round);
    void writeBack(vector<vector<char> > &board);
    void printMatrix(const size_t[][9]);
    void copyArray(const size_t src[][9], size_t dst[][9]);
};



const size_t Sudoku::values[] = {1,2,3,4,5,6,7,8,9};


void Sudoku::solveSudoku(vector<vector<char> > &board) {
    if (init(board)) {
        if (solve() == 1) {
            writeBack(board);
        }
    }
}


void Sudoku::solveSudoku(size_t q[][9]) {
    init(q);

    cout << "Question:" << endl;
    printMatrix(q);
    
    if (solve() == 1) {
        cout << "Solution:" << endl;
        printMatrix(d);
    }
}


void Sudoku::genSudoku() {
    size_t pos_x, pos_y;
    int ret = 0;

    do {
        for (pos_x = 0; pos_x < 9; pos_x++) {
            for (pos_y = 0; pos_y < 9; pos_y++) {
                q[pos_x][pos_y] = 0;
            }
        }
        
        init(q);
        ret = goDFS(0);
    } while (ret != 1);
    
#ifdef DEBUG
    cout << "Successfully generated question:" << endl;
    printMatrix(q);
    cout << "Answer:" << endl;
    printMatrix(d);
    cout << endl;
#endif
}


int Sudoku::goDFS(size_t round) {

    if (round > MAX_DEPTH)
        return 2;
    
    size_t pos, x, y, s_v, v;
#ifdef DEBUG
    cout << "Round " << round << endl;
#endif

    do {
        pos = rand() % 81;
        x = pos / 9;
        y = pos % 9;
    } while (d[x][y]);
    
    if (allow[x][y].size() == 0) {
        return -1;
    }
    
    // A temp set to save all possible values for this cell
    // Because the allow[x][y] will change in the running.
    set aset(allow[x][y]); 
    
    s_v = rand() % 9 + 1;
    v = s_v;
    while (! aset.count(v)) {
        v = v % 9 + 1;
    }

    do {
        q[x][y] = v;  // update question
        fill(x, y, v); // update current cell
#ifdef DEBUG
        cout << "Filled (" << x << ", " << y << ") = " << v << ";\t";
        printMatrix(d);
#endif
        
        int ret = 0;
        if (round >= START_TRY) {
#ifdef DEBUG
            ret = solve(false);
#else
            ret = solve(false, false);
#endif
        }
        
        if (ret == 0) { // finish
            ret = goDFS(round + 1);
            if (ret >= 1) {
                return ret;
            }
        } else if (ret == 1) { // Need to fill more cells
            return 1;
        }
        
        // rollback, and try next value.
        do {
            v = v % 9 + 1;
        } while ((v != s_v) && (! aset.count(v)));
        
        if (v != s_v) { // If there is another value to try in this round, reset.
            q[x][y] = 0;
            init(q);
        }

    } while (v != s_v);
    // Tried all the values, back to the last layer.
    q[x][y] = 0;
    return -1; // All value does not work well
}


bool Sudoku::init(vector<vector<char> > &board) {
    size_t pos_x, pos_y, value;
    char c;

    for (pos_x = 0; pos_x < 9; pos_x++) {
        for (pos_y = 0; pos_y < 9; pos_y++) {
            c = board[pos_x][pos_y];
            if (c == '.') {
                d[pos_x][pos_y] = 0;
            } else if ((c >= '1') && (c <= '9')){
                value = c - '0';
                d[pos_x][pos_y] = value;
            } else {
                cout << "Wrong data in row (" << pos_x + 1 << ", column " << pos_y + 1 << "), value = " << c << endl;
                return false;
            }
        }
    }
    preSet();

    cout << "Question:" << endl;
    printMatrix(d);
    return true;
}



void Sudoku::preSet() {
    size_t pos_x, pos_y, pos, value;

    for (size_t i = 0; i < 9; i++) {
        // clear
        row_free_index[i].clear();
        col_free_index[i].clear();
        squ_free_index[i].clear();

        // init
        row_free_value[i].full();
        col_free_value[i].full();
        squ_free_value[i].full();
    }
    
    for (pos_x = 0; pos_x < 9; pos_x++) {
        for (pos_y = 0; pos_y < 9; pos_y++) {
            allow[pos_x][pos_y].clear();

            value = d[pos_x][pos_y];
            if (value) {
                row_free_value[pos_y].erase(value);
                col_free_value[pos_x].erase(value);
                squ_free_value[(pos_x/3) * 3 + pos_y/3].erase(value);
            } else {
                pos = pos_x * 9 + pos_y;
                row_free_index[pos_y].insert(pos);
                col_free_index[pos_x].insert(pos);
                squ_free_index[(pos_x/3) * 3 + pos_y/3].insert(pos);
            }
        }
    }
    
    // Update the allowed values list for empty cells
    for (pos_x = 0; pos_x < 9; pos_x++) {
        for (pos_y = 0; pos_y < 9; pos_y++) {
            if (! d[pos_x][pos_y]) { // empty
#ifdef DEBUG
                cout << "allow[" << pos_x << "][" << pos_y << "] = ";
#endif
                set& aset = row_free_value[pos_y];
                size_t sz = aset.size();
                for (size_t i = 0; i < sz; i++) {
                    size_t v = aset.get(i);
                    if (col_free_value[pos_x].count(v) && squ_free_value[(pos_x/3) * 3 + pos_y/3].count(v)) {
                        allow[pos_x][pos_y].insert(v);
#ifdef DEBUG
                        cout << "," << (v);
#endif
                    }
                }
#ifdef DEBUG
                cout << ";\t";
#endif
            }
        }
    }
}    

    
int Sudoku::solve(bool solve, bool needInfo) {
    // Solve the problem
    size_t pos_x, pos_y, value;
    bool no_fill; // No cells being filled in one round, no Sudoku
    bool has_empty; // If false, the problem has been solved.
    
    do {
        no_fill = true; has_empty = false;
        for (pos_x = 0; pos_x < 9; pos_x++) {
            for (pos_y = 0; pos_y < 9; pos_y++) {
                if (! d[pos_x][pos_y]) { // empty
                    if (allow[pos_x][pos_y].size() == 0) {
                        if (needInfo) {
                            cout << "Error in given numbers, can not complete this game!" << endl;
                            printMatrix(d);
                        }
                        return -1;
                    } else if (canFill(pos_x, pos_y, value)) { // can fill with value
                        fill(pos_x, pos_y, value, solve);
                        no_fill = false;
                    } else {
                        has_empty = true;
                    }
                }
            }
        }
        if (has_empty && no_fill) {
            if (needInfo) {
                cout << "Need more known numbers in the data, can not complete!" << endl;
                printMatrix(d);
            }
            return 0;
        }
    } while (has_empty);
    
    return 1;
}
    
void Sudoku::writeBack(vector<vector<char> > &board) {
    cout << "Solution:" << endl;
    printMatrix(d);
    
    size_t pos_x, pos_y;//, pos, value;
    for (pos_x = 0; pos_x < 9; pos_x++) {
        for (pos_y = 0; pos_y < 9; pos_y++) {
            board[pos_x][pos_y] = d[pos_x][pos_y] + '0';
        }
    }
}


bool Sudoku::canFill(const size_t x, const size_t y, size_t& v) {
    if (allow[x][y].size() == 1) {
        v = allow[x][y].get(0);
#ifdef DEBUG
        cout << "only sel(" << x << ", " << y << ") = " << v << ";\t";
#endif
        return true;
    }
    
    size_t i = x * 9 + y;
    size_t i1 = (x/3) * 3 + y/3;
    size_t i2;
    bool only = true;
    
    // check row: whether some value can only be filled in this cell
    set& aset_v1 = row_free_value[y];
    for (size_t k = 0; k < aset_v1.size(); k++) {
        v = aset_v1.get(k);

        if (! allow[x][y].count(v)) continue;
        
        only = true;
        set& aset_i1 = row_free_index[y];
        for (size_t j = 0; j < aset_i1.size(); j++) {
            i2 = aset_i1.get(j);
            if (i != i2) {
                if (allow[i2 / 9][i2 % 9].count(v)) {
                    only = false;
                    break;
                }
            }
        }
        if (only) {
#ifdef DEBUG
            cout << "row sel(" << x << ", " << y << ") = " << v << ";\t";
#endif
            return true;
        }
    }
    
    // check column: whether some value can only be filled in this cell
    set& aset_v2 = col_free_value[x];
    for (size_t k = 0; k < aset_v2.size(); k++) {
        v = aset_v2.get(k);

        if (! allow[x][y].count(v)) continue;
        
        only = true;
        set& aset_i2 = col_free_index[x];
        for (size_t j = 0; j < aset_i2.size(); j++) {
            i2 = aset_i2.get(j);
            if (i != i2) {
                if (allow[i2 / 9][i2 % 9].count(v)) {
                    only = false;
                    break;
                }
            }
        }
        if (only) {
#ifdef DEBUG
            cout << "col sel(" << x << ", " << y << ") = " << v << ";\t";
#endif
            return true;
        }
    }
    
    // check square: whether some value can only be filled in this cell
    set& aset_v3 = squ_free_value[i1];
    for (size_t k = 0; k < aset_v3.size(); k++) {
        v = aset_v3.get(k);

        if (! allow[x][y].count(v)) continue;
        
        only = true;
        set& aset_i3 = squ_free_index[i1];
        for (size_t j = 0; j < aset_i3.size(); j++) {
            i2 = aset_i3.get(j);
            if (i != i2) {
                if (allow[i2 / 9][i2 % 9].count(v)) {
                    only = false;
                    break;
                }
            }
        }
        if (only) {
#ifdef DEBUG
            cout << "squ sel(" << x << ", " << y << ") = " << v << ";\t";
#endif
            return true;
        }
    }
    
    return false;
}


void Sudoku::fill(const size_t pos_x, const size_t pos_y, const size_t v, bool solve) {
    // Fill in the value;
    d[pos_x][pos_y] = v;
#ifdef DEBUG
    cout << "Filled (" << pos_x << ", " << pos_y << ") = " << v << ";\t";
#endif
    
    // Update the free values and free indexs
    size_t i = pos_x * 9 + pos_y;
    size_t i1 = (pos_x/3) * 3 + pos_y/3;
    size_t i2;
    row_free_index[pos_y].erase(i);
    col_free_index[pos_x].erase(i);
    squ_free_index[i1].erase(i);

    row_free_value[pos_y].erase(v);
    col_free_value[pos_x].erase(v);
    squ_free_value[i1].erase(v);

/*
#ifdef DEBUG
    if (row_free_index[pos_y].size() != row_free_value[pos_y].size()) {
        cout << "row_free_index[" << pos_y << "].size() != row_free_value[" << pos_y << "].size()";
        printMatrix(d);
    }
    if (col_free_index[pos_x].size() != col_free_value[pos_x].size()) {
        cout << "col_free_index[" << pos_x << "].size() != col_free_value[" << pos_x << "].size()";
        printMatrix(d);
    }
    if (squ_free_index[i1].size() != squ_free_value[i1].size()) {
        cout << "squ_free_index[" << i1 << "].size() != squ_free_value[" << i1 << "].size()";
        printMatrix(d);
    }
#endif    
*/

    set& aset_i1 = row_free_index[pos_y];
    for (size_t k = 0; k < aset_i1.size(); k++) {
        i2 = aset_i1.get(k);
        allow[i2 / 9][i2 % 9].erase(v);
    }

    set& aset_i2 = col_free_index[pos_x];
    for (size_t k = 0; k < aset_i2.size(); k++) {
        i2 = aset_i2.get(k);
        allow[i2 / 9][i2 % 9].erase(v);
    }

    set& aset_i3 = squ_free_index[i1];
    for (size_t k = 0; k < aset_i3.size(); k++) {
        i2 = aset_i3.get(k);
        allow[i2 / 9][i2 % 9].erase(v);
    }

    if (solve) {
        // Fill in the nearby cells if only one value left
        if (row_free_value[pos_y].size() == 1) {
            i = row_free_index[pos_y].get(0);
            if (! d[i/9][i%9])
                fill(i/9, i%9, row_free_value[pos_y].get(0));
        }
        
        
        if (col_free_value[pos_x].size() == 1) {
            i = col_free_index[pos_x].get(0);
            if (! d[i/9][i%9])
                fill(i/9, i%9, col_free_value[pos_x].get(0));
        }
        
        
        if (squ_free_value[i1].size() == 1) {
            i = squ_free_index[i1].get(0);
            if (! d[i/9][i%9])
                fill(i/9, i%9, squ_free_value[i1].get(0));
        }
    }
};


void Sudoku::printMatrix(const size_t m[][9]) {
    for (size_t pos_x = 0; pos_x < 9; pos_x++) {
        for (size_t pos_y = 0; pos_y < 9; pos_y++) {
            cout << "\t" << m[pos_x][pos_y];
        }
        cout << endl;
    }
}


void Sudoku::copyArray(const size_t src[][9], size_t dst[][9]) {
    size_t pos_x, pos_y;

    for (pos_x = 0; pos_x < 9; pos_x++) {
        for (pos_y = 0; pos_y < 9; pos_y++) {
            dst[pos_x][pos_y] = src[pos_x][pos_y];
        }
    }
}






int main() {
//    Sudoku Sudoku;
    
    struct timeval tv;
    gettimeofday(&tv, NULL);
    srand(tv.tv_usec);
    
    /*
    char str[100];
    cout << "Please input a string representation of the Sudoku: " << endl;
    cin >> str;
    
    vector<vector<char> > b;
    vector<char>::iterator it;
    vector<vector<char> >::iterator bit = b.begin();
    for (int x = 0; x < 9; x++) {
        vector<char> vc;
        it = vc.end();
        for (int y = 0; y < 9; y++) {
            it = vc.insert(vc.end(), str[x * 9 + y]);
        }
        bit = b.insert(b.end(), vc);
    }
    
    Sudoku.solveSudoku(b);
    */
    
    for (size_t i = 0; i < TRY_COUNT; i++) {
    Sudoku s;
    size_t q[9][9];
    s.getQuestion(q);
    s.solveSudoku(q);
    }

}
