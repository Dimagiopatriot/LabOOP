package lab1;

/**
 * Created by troll on 3/23/2017.
 */
public class Main {
    public static void main(String[] args) {
        Memory m = new Memory(1000);
        int maxSize = 100;
        int n = 100;
        int[] ptr = new int[n];
        int[] controlSum = new int[n];
        for (int i = 0; i < ptr.length; i++)
            ptr[i] = -1;
        for (int i = 0; i < 1000000; i++) {
            int index = (int) (Math.random() * (n - 1));
            m.blocks();
            System.out.println(i + ") ");
            if (ptr[index] == -1) {
                int size = (int) (Math.random() * maxSize);
                ptr[index] = m.memAllocate(size);
                m.fillBlock(ptr[index]);
                controlSum[index] = m.getControlSum(ptr[index]);
            } else {
                if (Math.random() > 0.5) {
                    m.memFree(ptr[index]);
                    ptr[index] = -1;
                } else {
                    int t = m.memReallocate(ptr[index],
                            (int) (Math.random() * maxSize));
                    if (t != -1) {
                        int s = m.getControlSum(t);
                        if ((s != controlSum[index]) && (t != ptr[index])
                                && (s > controlSum[index])) {
                            System.err.println("Error control sum");
                            break;
                        }
                        ptr[index] = t;
                        m.fillBlock(ptr[index]);
                        controlSum[index] = m.getControlSum(ptr[index]);
                    }
                }
            }
            if (m.testPtrs() == -1)
                break;
        }
    }

}
