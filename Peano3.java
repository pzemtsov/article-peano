import java.util.HashSet;

public class Peano3
{
    public static Peano3 ZERO = new Peano3 ();
    public static Peano3 ONE = ZERO.S();
    public static Peano3 TWO = ONE.S();
    public static Peano3 THREE = TWO.S();
    public static Peano3 FOUR = THREE.S();
    public static Peano3 FIVE = FOUR.S();
    public static Peano3 SIX = FIVE.S();
    public static Peano3 SEVEN = SIX.S();
    public static Peano3 EIGHT = SEVEN.S();
    public static Peano3 NINE = EIGHT.S();
    public static Peano3 TEN = NINE.S();
    
    private final HashSet<Peano3> set;
    private Peano3 next = null;
    
    private Peano3 (HashSet<Peano3> set)
    {
        this.set = set;
    }

    private Peano3 ()
    {
        this (new HashSet<Peano3> ());
    }

    public boolean isZero ()
    {
        return this == ZERO;
    }

    public Peano3 S ()
    {
        if (next == null) {
            HashSet<Peano3> newSet = new HashSet<Peano3> ();
            newSet.addAll (set);
            newSet.add (this);
            next = new Peano3 (newSet);
        }
        return next;
    }

    public Peano3 D ()
    {
        if (isZero ()) throw new IllegalArgumentException ("D called on Zero");
        Peano3 max = null;
        for (Peano3 x : set)
        {
            if (max == null || x.set.contains (max)) {
                max = x;
            }
        }
        return max;
    }

    public boolean lt (Peano3 other)
    {
        return other.set.contains (this);
    }

    public boolean gt (Peano3 other)
    {
        return other.lt (this);
    }
    
    public boolean leq (Peano3 other)
    {
        return ! gt (other);
    }
    
    public boolean geq (Peano3 other)
    {
        return ! lt (other);
    }

    public Peano3 plus (Peano3 other)
    {
        return other.isZero () ? this : plus (other.D ()).S ();
    }

    public Peano3 minus (Peano3 other)
    {
        return other.isZero () ? this : minus (other.D ()).D ();
    }

    public Peano3 mult (Peano3 other)
    {
        return other.isZero () ? ZERO : mult (other.D ()).plus (this);
    }
    
    public Peano3 square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Peano3 quotient;
        final Peano3 remainder;
        
        public DivResult (Peano3 quotient, Peano3 remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Peano3 other)
    {
        Peano3 div = ZERO;
        Peano3 rem = this;
        while (rem.geq (other)) {
            rem = rem.minus (other);
            div = div.S ();
        }
        return new DivResult (div, rem);
    }

    public Peano3 div (Peano3 other)
    {
        return divrem (other).quotient;
    }

    public Peano3 rem (Peano3 other)
    {
        return divrem (other).remainder;
    }

    private static String digitToString (Peano3 p)
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

    private static Peano3 charToDigit (char c)
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
    
    public static Peano3 parseInt (String s)
    {
        Peano3 result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Peano3 N)
    {
        Debug.Timer.start ();
        for (Peano3 c = ONE; c.leq (N); c = c.S ())
            for (Peano3 b = ONE; b.lt (c); b = b.S ())
                for (Peano3 a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void perfect (Peano3 N)
    {
        Debug.Timer.start ();
        for (Peano3 i = ONE; i.leq (N); i = i.S ()) {
            Peano3 sum = ZERO;
            for (Peano3 j = ONE; j.lt (i); j = j.S ())
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
        Peano3 x = ZERO;
        Debug.Timer.start ();
        while (true) {
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
        pythagorean (Peano3.parseInt ("1000"));
        perfect (Peano3.parseInt ("1000"));
    }
}