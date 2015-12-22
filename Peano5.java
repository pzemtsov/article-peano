
public class Peano5
{
    public static Peano5 ZERO = new Peano5 ();
    public static Peano5 ONE = ZERO.S();
    public static Peano5 TWO = ONE.S();
    public static Peano5 THREE = TWO.S();
    public static Peano5 FOUR = THREE.S();
    public static Peano5 FIVE = FOUR.S();
    public static Peano5 SIX = FIVE.S();
    public static Peano5 SEVEN = SIX.S();
    public static Peano5 EIGHT = SEVEN.S();
    public static Peano5 NINE = EIGHT.S();
    public static Peano5 TEN = NINE.S();
    
    private Peano5 next = null;
    private final Peano5 prev;
    
    private Peano5 (Peano5 prev)
    {
        this.prev = prev;
    }

    private Peano5 ()
    {
        this (null);
    }

    public boolean isZero ()
    {
        return this == ZERO;
    }

    public Peano5 S ()
    {
        if (next == null) {
            next = new Peano5 (this);
        }
        return next;
    }

    public Peano5 D ()
    {
        if (isZero ()) throw new IllegalArgumentException ("D called on Zero");
        return prev;
    }
    
    public boolean lt (Peano5 other)
    {
        if (this == other) return false;
        Peano5 p = this;
        Peano5 q = other;
        while (true) {
            p = p.next;
            if (p == null) return false;
            if (p == other) return true;
            q = q.next;
            if (q == null) return true;
            if (q == this) return false;
        }
    }

    public boolean gt (Peano5 other)
    {
        return other.lt (this);
    }
    
    public boolean leq (Peano5 other)
    {
        return ! gt (other);
    }
    
    public boolean geq (Peano5 other)
    {
        return ! lt (other);
    }

    public Peano5 plus (Peano5 other)
    {
        return other.isZero () ? this : plus (other.D ()).S ();
    }

    public Peano5 minus (Peano5 other)
    {
        return other.isZero () ? this : minus (other.D ()).D ();
    }

    public Peano5 mult (Peano5 other)
    {
        return other.isZero () ? ZERO : mult (other.D ()).plus (this);
    }
    
    public Peano5 square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Peano5 quotient;
        final Peano5 remainder;
        
        public DivResult (Peano5 quotient, Peano5 remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Peano5 other)
    {
        Peano5 div = ZERO;
        Peano5 rem = this;
        while (rem.geq (other)) {
            rem = rem.minus (other);
            div = div.S ();
        }
        return new DivResult (div, rem);
    }

    public Peano5 div (Peano5 other)
    {
        return divrem (other).quotient;
    }

    public Peano5 rem (Peano5 other)
    {
        return divrem (other).remainder;
    }

    private static String digitToString (Peano5 p)
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

    private static Peano5 charToDigit (char c)
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
    
    public static Peano5 parseInt (String s)
    {
        Peano5 result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Peano5 N)
    {
        Debug.Timer.start ();
        for (Peano5 c = ONE; c.leq (N); c = c.S ())
            for (Peano5 b = ONE; b.lt (c); b = b.S ())
                for (Peano5 a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void perfect (Peano5 N)
    {
        Debug.Timer.start ();
        for (Peano5 i = ONE; i.leq (N); i = i.S ()) {
            Peano5 sum = ZERO;
            for (Peano5 j = ONE; j.lt (i); j = j.S ())
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
        Peano5 x = ZERO;
        Debug.Timer.start ();
        for (int i = 0; i < 1000000; i++)
            x = x.S ();
        Debug.Timer.stop ();
        Debug.Timer.dump ();
        System.out.println (x);
        Debug.Timer.stop ();
        Debug.Timer.dump ();
        Debug.dumpMemory ();
    }
    
    public static void main (String [] args)
    {
        inctest ();
        pythagorean (Peano5.parseInt ("1000"));
        perfect (Peano5.parseInt ("10000"));
    }
}