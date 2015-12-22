
public class Assignment
{
    public static void pithagorean (int N)
    {
        Debug.Timer.start ();
        for (int c = 1; c <= N; c++)
            for (int b = 1; b < c; b++)
                for (int a = 1; a < b; a++)
                    if (a*a + b*b == c*c) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
        
    }
    
    public static void perfect (int N)
    {
        Debug.Timer.start ();
        for (int i = 1; i <= N; i++) {
            int sum = 0;
            for (int j = 1; j < i; j++)
                if (i % j == 0)
                    sum += j;
            if (i == sum) {
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
            }
        }
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }
    
    public static void main (String [] args)
    {
        pithagorean (10000);
        perfect (100000);
    }
}
