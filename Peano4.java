import java.util.HashSet;

public class Peano4
{
    public static Peano4 ZERO = new Peano4 ();
    public static Peano4 ONE = ZERO.S();
    public static Peano4 TWO = ONE.S();
    public static Peano4 THREE = TWO.S();
    public static Peano4 FOUR = THREE.S();
    public static Peano4 FIVE = FOUR.S();
    public static Peano4 SIX = FIVE.S();
    public static Peano4 SEVEN = SIX.S();
    public static Peano4 EIGHT = SEVEN.S();
    public static Peano4 NINE = EIGHT.S();
    public static Peano4 TEN = NINE.S();
    
    private final HashSet<Peano4> set;
    private Peano4 next = null;
    private final Peano4 prev;
    
    private Peano4 (Peano4 prev, HashSet<Peano4> set)
    {
        this.prev = prev;
        this.set = set;
    }

    private Peano4 ()
    {
        this (null, new HashSet<Peano4> ());
    }

    public boolean isZero ()
    {
        return this == ZERO;
    }

    public Peano4 S ()
    {
        if (next == null) {
            HashSet<Peano4> newSet = new HashSet<Peano4> ();
            newSet.addAll (set);
            newSet.add (this);
            next = new Peano4 (this, newSet);
        }
        return next;
    }

    public Peano4 D ()
    {
        if (isZero ()) throw new IllegalArgumentException ("D called on Zero");
        return prev;
    }
    
    public boolean lt (Peano4 other)
    {
        return other.set.contains (this);
    }

    public boolean gt (Peano4 other)
    {
        return other.lt (this);
    }
    
    public boolean leq (Peano4 other)
    {
        return ! gt (other);
    }
    
    public boolean geq (Peano4 other)
    {
        return ! lt (other);
    }

    public Peano4 plus (Peano4 other)
    {
        return other.isZero () ? this : plus (other.D ()).S ();
    }

    public Peano4 minus (Peano4 other)
    {
        return other.isZero () ? this : minus (other.D ()).D ();
    }

    public Peano4 mult (Peano4 other)
    {
        return other.isZero () ? ZERO : mult (other.D ()).plus (this);
    }
    
    public Peano4 square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Peano4 quotient;
        final Peano4 remainder;
        
        public DivResult (Peano4 quotient, Peano4 remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Peano4 other)
    {
        Peano4 div = ZERO;
        Peano4 rem = this;
        while (rem.geq (other)) {
            rem = rem.minus (other);
            div = div.S ();
        }
        return new DivResult (div, rem);
    }

    public Peano4 div (Peano4 other)
    {
        return divrem (other).quotient;
    }

    public Peano4 rem (Peano4 other)
    {
        return divrem (other).remainder;
    }

    private static String digitToString (Peano4 p)
    {
        if (p.equals (ZERO))  return "0";
        if (p.equals (ONE))   return "1";
        if (p.equals (TWO))   return "2";
        if (p.equals (THREE)) return "3";
        if (p.equals (FOUR))  return "4";
        if (p.equals (FIVE))  return "5";
        if (p.equals (SIX))   return "6";
        if (p.equals (SEVEN)) return "7";
        if (p.equals (EIGHT)) return "8";
        if (p.equals (NINE))  return "9";
        throw new IllegalArgumentException ();
    }

    private static Peano4 charToDigit (char c)
    {
        switch (c) {
        case '0' : return ZERO;
        case '1' : return ONE;
        case '2' : return TWO;
        case '3' : return THREE;
        case '4' : return FOUR;
        case '5' : return FIVE;
        case '6' : return SIX;
        case '7' : return SEVEN;
        case '8' : return EIGHT;
        case '9' : return NINE;
        default:
            throw new IllegalArgumentException ();
        }
    }
    
    @Override
    public String toString ()
    {
        DivResult d = this.divrem (TEN);
        String digit = digitToString (d.remainder);
        return d.quotient.isZero () ? digit : d.quotient + digit;
    }
    
    public static Peano4 parseInt (String s)
    {
        Peano4 result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Peano4 N)
    {
        Debug.Timer.start ();
        for (Peano4 c = ONE; c.leq (N); c = c.S ())
            for (Peano4 b = ONE; b.lt (c); b = b.S ())
                for (Peano4 a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void perfect (Peano4 N)
    {
        Debug.Timer.start ();
        for (Peano4 i = ONE; i.leq (N); i = i.S ()) {
            Peano4 sum = ZERO;
            for (Peano4 j = ONE; j.lt (i); j = j.S ())
                if (i.rem (j).isZero ())
                    sum = sum.plus (j);
            if (i.equals (sum)) {
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
            }
        }
        System.out.println ("Total: ");
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }

    static void inctest ()
    {
        Peano4 x = ZERO;
        Debug.Timer.start ();
        while (true) {
            for (int i = 0; i < 100; i++)
                x = x.S ();
            System.out.println (x);
            Debug.Timer.stop ();
            Debug.Timer.dump ();
            Debug.dumpMemory ();
        }
    }
    
    public static void main (String [] args)
    {
        inctest ();
        pythagorean (Peano4.parseInt ("1000"));
        perfect (Peano4.parseInt ("10000"));
    }
}