
public class Debug
{
    public static class Timer
    {
        static long time0;
        static long time1;
        
        public static void start ()
        {
            time0 = System.currentTimeMillis ();
        }
        
        public static void stop ()
        {
            time1 = System.currentTimeMillis ();
        }
        
        public static void dump ()
        {
            System.out.println (time1 - time0);
        }
    }
    
    public static class Counter
    {
        private long value = 0;
        
        public void inc ()
        {
            ++ value;
        }
        
        public void dump ()
        {
            System.out.println (value);
        }
    }

    public static void dumpMemory ()
    {
        Runtime r = Runtime.getRuntime ();
        System.out.println ("Used memory: " + (r.totalMemory () - r.freeMemory ()));
    }
}
