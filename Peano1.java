import java.util.HashSet;

public class Peano1 extends HashSet<Peano1>
{
    public static Peano1 ZERO = new Peano1 ();
    public static Peano1 ONE = ZERO.S();
    public static Peano1 TWO = ONE.S();
    public static Peano1 THREE = TWO.S();
    public static Peano1 FOUR = THREE.S();
    public static Peano1 FIVE = FOUR.S();
    public static Peano1 SIX = FIVE.S();
    public static Peano1 SEVEN = SIX.S();
    public static Peano1 EIGHT = SEVEN.S();
    public static Peano1 NINE = EIGHT.S();
    public static Peano1 TEN = NINE.S();
    
    private Peano1 ()
    {
        super ();
    }

    private Peano1 (Peano1 other)
    {
        super (other);
    }
    
    public Peano1 (String str)
    {
        this (parseInt (str));
    }

    public boolean isZero ()
    {
        return this.equals (ZERO);
    }
    
    public Peano1 S ()
    {
        Peano1 s = new Peano1 ();
        s.addAll (this);
        s.add (this);
        return s;
    }

    public Peano1 D ()
    {
        if (isZero ()) throw new IllegalArgumentException ("D called on Zero");
        Peano1 max = null;
        for (Peano1 x : this)
        {
            if (max == null || x.contains (max)) {
                max = x;
            }
        }
        return max;
    }

    public boolean lt (Peano1 other)
    {
        return other.contains (this);
    }

    public boolean gt (Peano1 other)
    {
        return other.lt (this);
    }
    
    public boolean leq (Peano1 other)
    {
        return ! gt (other);
    }
    
    public boolean geq (Peano1 other)
    {
        return ! lt (other);
    }

    public Peano1 plus (Peano1 other)
    {
        return other.isZero () ? this : plus (other.D ()).S ();
    }

    public Peano1 minus (Peano1 other)
    {
        return other.isZero () ? this : minus (other.D ()).D ();
    }

    public Peano1 mult (Peano1 other)
    {
        return other.isZero () ? ZERO : mult (other.D ()).plus (this);
    }
    
    public Peano1 square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Peano1 quotient;
        final Peano1 remainder;
        
        public DivResult (Peano1 quotient, Peano1 remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Peano1 other)
    {
        Peano1 div = ZERO;
        Peano1 rem = this;
        while (rem.geq (other)) {
            rem = rem.minus (other);
            div = div.S ();
        }
        return new DivResult (div, rem);
    }

    public Peano1 div (Peano1 other)
    {
        return divrem (other).quotient;
    }

    public Peano1 rem (Peano1 other)
    {
        return divrem (other).remainder;
    }

    private static String digitToString (Peano1 p)
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

    private static Peano1 charToDigit (char c)
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
    
    private static Peano1 parseInt (String s)
    {
        Peano1 result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Peano1 N)
    {
        Debug.Timer.start ();
        for (Peano1 c = ONE; c.leq (N); c = c.S ())
            for (Peano1 b = ONE; b.lt (c); b = b.S ())
                for (Peano1 a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void perfect (Peano1 N)
    {
        Debug.Timer.start ();
        for (Peano1 i = ONE; i.leq (N); i = i.S ()) {
            Peano1 sum = ZERO;
            for (Peano1 j = ONE; j.lt (i); j = j.S ())
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
    
    public static void main (String [] args)
    {
        pythagorean (new Peano1 ("100"));
        perfect (new Peano1 ("100"));
    }
}