import java.util.HashSet;

public class Peano2 extends HashSet<Peano2>
{
    public static Peano2 ZERO = new Peano2 ();
    public static Peano2 ONE = ZERO.S();
    public static Peano2 TWO = ONE.S();
    public static Peano2 THREE = TWO.S();
    public static Peano2 FOUR = THREE.S();
    public static Peano2 FIVE = FOUR.S();
    public static Peano2 SIX = FIVE.S();
    public static Peano2 SEVEN = SIX.S();
    public static Peano2 EIGHT = SEVEN.S();
    public static Peano2 NINE = EIGHT.S();
    public static Peano2 TEN = NINE.S();
    
    private Peano2 next = null;
    
    private Peano2 ()
    {
        super ();
    }

    public boolean isZero ()
    {
        return this == ZERO;
    }

    @Override
    public boolean equals (Object o)
    {
        return this == o;
    }
    
    public Peano2 S ()
    {
        if (next == null) {
            Peano2 s = new Peano2 ();
            s.addAll (this);
            s.add (this);
            next = s;
        }
        return next;
    }

    public Peano2 D ()
    {
        if (isZero ()) throw new IllegalArgumentException ("D called on Zero");
        Peano2 max = null;
        for (Peano2 x : this)
        {
            if (max == null || x.contains (max)) {
                max = x;
            }
        }
        return max;
    }

    public boolean lt (Peano2 other)
    {
        return other.contains (this);
    }

    public boolean gt (Peano2 other)
    {
        return other.lt (this);
    }
    
    public boolean leq (Peano2 other)
    {
        return ! gt (other);
    }
    
    public boolean geq (Peano2 other)
    {
        return ! lt (other);
    }

    public Peano2 plus (Peano2 other)
    {
        return other.isZero () ? this : plus (other.D ()).S ();
    }

    public Peano2 minus (Peano2 other)
    {
        return other.isZero () ? this : minus (other.D ()).D ();
    }

    public Peano2 mult (Peano2 other)
    {
        return other.isZero () ? ZERO : mult (other.D ()).plus (this);
    }
    
    public Peano2 square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Peano2 quotient;
        final Peano2 remainder;
        
        public DivResult (Peano2 quotient, Peano2 remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Peano2 other)
    {
        Peano2 div = ZERO;
        Peano2 rem = this;
        while (rem.geq (other)) {
            rem = rem.minus (other);
            div = div.S ();
        }
        return new DivResult (div, rem);
    }

    public Peano2 div (Peano2 other)
    {
        return divrem (other).quotient;
    }

    public Peano2 rem (Peano2 other)
    {
        return divrem (other).remainder;
    }

    private static String digitToString (Peano2 p)
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

    private static Peano2 charToDigit (char c)
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
    
    public static Peano2 parseInt (String s)
    {
        Peano2 result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Peano2 N)
    {
        Debug.Timer.start ();
        for (Peano2 c = ONE; c.leq (N); c = c.S ())
            for (Peano2 b = ONE; b.lt (c); b = b.S ())
                for (Peano2 a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void perfect (Peano2 N)
    {
        Debug.Timer.start ();
        for (Peano2 i = ONE; i.leq (N); i = i.S ()) {
            Peano2 sum = ZERO;
            for (Peano2 j = ONE; j.lt (i); j = j.S ())
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
        Peano2 x = ZERO;
        while (true) {
            Debug.Timer.start ();
            x = x.S ();
            System.out.println (x);
            Debug.Timer.stop ();
            Debug.Timer.dump ();
        }
    }
    
    public static void main (String [] args)
    {
        inctest ();
        pythagorean (Peano2.parseInt ("100"));
        perfect (Peano2.parseInt ("100"));
    }
}