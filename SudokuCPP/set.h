class set {
  public:
    set();
    bool count(size_t x);
    size_t size() {return sz;};
    size_t get(size_t index);
    void insert(size_t x);
    void erase(size_t x);
    void full();
    void clear();
  private:
    size_t v[9];
    size_t sz;
};


set::set() {
    clear();
}


void set::full() {
    sz = 9;
    for (size_t i = 0; i < 9; i++)
        v[i] = i + 1;
}


void set::insert(size_t x) {
    sz++;
    size_t i = 0;
    while (v[i]) i++;
    v[i] = x;
}


void set::erase(size_t x) {
    if (sz == 0) return;
    
    size_t i = 0;
    while (i < 9) {
        if (v[i] == x) {
            v[i] = 0;
            sz--;
            return;
        }
        i++;
    }
}


void set::clear() {
    sz = 0;
    for (size_t i = 0; i < 9; i++)
        v[i] = 0;
}


bool set::count(size_t x) {
    size_t i = 0;
    while (i < 9) {
        if (v[i] == x) return true;
        i++;
    }
    return false;
}


size_t set::get(size_t index) {
    size_t i = 0, j = 0;
    while (i < 9) {
        if (v[i]) {
            if (index == j) return v[i];
            j++;
        }
        i++;
    }
    return 0;
}



