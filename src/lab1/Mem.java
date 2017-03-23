package lab1;

/**
 * Created by troll on 3/23/2017.
 */
public class Mem {
    public Mem(int memSize) {
        if (memSize < 10)
            memSize = 10;
        memory = new int[memSize];
        memory[0] = 1;
        memory[1] = 3;
        memory[2] = 0;
        memory[4] = memSize - 6;
        memory[5] = 3;
        memory[memSize - 3] = 1;
        memory[memSize - 2] = 0;
        memory[memSize - 1] = memSize - 3;

    }

    public int mem_alloc(int size) {
        if (size < 1)
            size = 1;
        int index = getNextPtr(0);
        int ptr = -1;
        while (getCs(index) != 0) {
            if ((getH(index) != 1) && ((getCs(index) - 3) >= size)) {
                setH(index, 1);
                if ((getCs(index) - 7) >= size) {
                    int Nptr = index + size + 3;
                    int next = getNextPtr(index);
                    int dCs = next - Nptr;
                    int dPs = Nptr - index;
                    setH(Nptr, 0);
                    setPs(next, dCs);
                    setCs(Nptr, dCs);
                    setCs(index, dPs);
                    setPs(Nptr, dPs);
                }
                return index;
            }
            index = getNextPtr(index);
        }
        return ptr;
    }

    public void mem_free(int ptr) {
        int ps = getPrevPtr(ptr);
        if (getH(ps) != 1) {
            ps = getPrevPtr(ps);
            if (getH(ps) != 1)
                System.err.println("ERROR ps ");
        }
        int cs = getNextPtr(ptr);
        if (getH(cs) != 1) {
            cs = getNextPtr(cs);
            if (getH(cs) != 1)
                System.err.println("ERROR cs ");
        }
        int freePtr = getNextPtr(ps);
        setH(freePtr, 0);
        int dCs = cs - freePtr;
        setCs(freePtr, dCs);
        setPs(cs, dCs);
        setH(ptr, 0);
        for (int i = 3; i < getCs(freePtr); i++)
            memory[freePtr + i] = 0;

    }

    public int mem_realloc(int ptr, int size) {
        if (size == 0)
            size = 1;
        int csize = getCs(ptr) - 3;
        int d = Math.abs(csize - size);
        if (csize == size)
            return ptr;
        if (csize > size) {
            int index = ptr + size + 3;
            int next = getNextPtr(ptr);
            if (getH(next) != 1) {
                int oldCs = getCs(next);
                int oldPs = getPs(next);
                setH(index, 0);
                setCs(index, oldCs + d);
                setPs(index, oldPs - d);
                setCs(ptr, getCs(ptr) - d);
                next = getNextPtr(index);
                setPs(next, getPs(next) + d);
                for (int i = 3; i < getCs(index); i++)
                    memory[index + i] = 0;
            } else {
                if (d > 3) {
                    setH(index, 0);
                    setCs(index, next - index);
                    setPs(index, size + 3);
                    setPs(next, next - index);
                    setCs(ptr, size + 3);
                    for (int i = 3; i < getCs(index); i++)
                        memory[index + i] = 0;
                }
            }
            return ptr;
        } else {
            int next = getNextPtr(ptr);
            if (getH(next) != 1) {
                if (getCs(next) >= d) {
                    if ((getCs(next) - d) > 3) {
                        int oldCs = getCs(next);
                        int oldPs = getPs(next);
                        next += d;
                        setCs(next, oldCs - d);
                        setPs(next, oldPs + d);
                        setH(next, 0);
                        setCs(ptr, getCs(ptr) + d);
                        next = getNextPtr(next);
                        setPs(next, getPs(next) - d);
                        for (int i = getCs(ptr) - d; i < getCs(ptr); i++)
                            memory[ptr + i] = 0;
                    } else {
                        int oldCs = getCs(ptr);
                        int cs = getCs(ptr) + getCs(next);
                        setCs(ptr, cs);
                        next = getNextPtr(ptr);
                        setPs(next, cs);
                        for (int i = oldCs; i < getCs(ptr); i++)
                            memory[ptr + i] = 0;
                    }
                } else {
                    int prev = getPrevPtr(ptr);
                    if (getH(prev) != 1) {
                        if (getCs(next) + getCs(prev) >= d) {
                            if ((getCs(next) + getCs(prev) - d) > 3) {
                                int oldCs = getCs(ptr);
                                int delta = d - getCs(next);
                                setCs(prev, getCs(prev) - delta);
                                setPs(ptr, getPs(ptr) - delta);
                                setCs(ptr, getCs(ptr) + d);
                                next = getNextPtr(next);
                                setPs(next, getCs(ptr));
                                for (int i = ptr; i < ptr + oldCs; i++)
                                    memory[i - delta] = memory[i];
                                ptr -= delta;
                                for (int i = oldCs; i < getCs(ptr); i++)
                                    memory[ptr + i] = 0;
                            } else {
                                int t = getCs(prev) + getCs(ptr) + getCs(next);
                                int oldCs = getCs(ptr);
                                int oldPs = getPs(ptr);
                                setCs(ptr, t);
                                setPs(ptr, getPs(prev));
                                next = getNextPtr(next);
                                setPs(next, t);
                                for (int i = ptr; i < ptr + oldCs; i++)
                                    memory[i - oldPs] = memory[i];
                                ptr -= oldPs;
                                for (int i = oldCs; i < getCs(ptr); i++)
                                    memory[ptr + i] = 0;
                            }
                        } else {
                            int t = mem_alloc(size);
                            if (t != -1) {
                                for (int i = 3; i < getCs(ptr); i++)
                                    memory[t + i] = memory[ptr + i];
                                mem_free(ptr);
                                return t;
                            }
                            return ptr;
                        }
                    }
                }
                return ptr;
            } else if (getH(getPrevPtr(ptr)) != 1) {
                int prev = getPrevPtr(ptr);
                if (getPs(ptr) >= d) {
                    if ((getPs(ptr) - d) > 3) {
                        int oldCs = getCs(ptr);
                        setCs(prev, getCs(prev) - d);
                        setCs(ptr, getCs(ptr) + d);
                        setPs(ptr, getPs(ptr) - d);
                        setPs(next, getPs(next) + d);
                        for (int i = ptr; i < ptr + oldCs; i++)
                            memory[i - d] = memory[i];
                        ptr -= d;
                        for (int i = oldCs; i < getCs(ptr); i++)
                            memory[ptr + i] = 0;
                    } else {
                        setH(prev, 1);
                        int cs = getCs(prev);
                        int oldCs = getCs(ptr);
                        setCs(prev, getCs(ptr) + cs);
                        setPs(next, getCs(ptr) + cs);
                        for (int i = ptr + 3; i < next; i++)
                            memory[i - cs] = memory[i];
                        for (int i = oldCs; i < getCs(prev); i++)
                            memory[prev + i] = 0;
                        return prev;
                    }
                } else {
                    int t = mem_alloc(size);
                    if (t != -1) {
                        for (int i = 3; i < getCs(ptr); i++)
                            memory[t + i] = memory[ptr + i];
                        mem_free(ptr);
                        return t;
                    }
                }
                return ptr;
            }
            return ptr;
        }

    }

    private int getNextPtr(int ptr) {
        return ptr + memory[ptr + 1];
    }

    private int getPrevPtr(int ptr) {
        return ptr - memory[ptr + 2];
    }

    private int getCs(int ptr) {
        return memory[ptr + 1];
    }

    private int getPs(int ptr) {
        return memory[ptr + 2];
    }

    private int getH(int ptr) {
        return memory[ptr];
    }

    private void setCs(int ptr, int value) {
        memory[ptr + 1] = value;
    }

    private void setPs(int ptr, int value) {
        memory[ptr + 2] = value;
    }

    private void setH(int ptr, int value) {
        memory[ptr] = value;
    }

    public void blocks() {
        int ptr = 0;
        int t = 0;
        while (getCs(ptr) != 0) {
            System.out.print(getH(ptr) + "| " + getCs(ptr) + "| " + getPs(ptr)
                    + "| ");
            ptr = getNextPtr(ptr);
            if (t > memory.length) {
                System.out.println();
                break;
            }
            t++;
        }
        System.out.println(getH(ptr) + " " + getCs(ptr) + " " + getPs(ptr)
                + " ");
    }

    public int testPtrs() {
        int ptr = getNextPtr(0);
        int prev = 0;
        int t = 0;
        while (ptr != memory.length - 3) {
            if (prev != getPrevPtr(ptr)) {
                System.err.println(" prev ");
                return -1;
            }
            if (getCs(ptr) < 4) {
                System.err.println(" next < 4 ");
                return -1;
            }
            if (t > memory.length) {
                System.err.println(" t > length ");
                return -1;
            }
            if ((getH(prev) == 0) && (getH(ptr) == 0)) {
                System.err.println(" heads = 0 ");
                return -1;
            }
            ptr = getNextPtr(ptr);
            prev = getNextPtr(prev);
            t++;
        }
        return 0;
    }

    public void fillBlock(int ptr) {
        int maxValue = 100;
        for (int i = 3; i < getCs(ptr); i++)
            memory[ptr + i] = (int) (Math.random() * maxValue);
    }

    public int getControlSum(int ptr) {
        int s = 0;
        for (int i = 3; i < getCs(ptr); i++)
            s += memory[ptr + i];
        return s;
    }

    private int[] memory;

}
