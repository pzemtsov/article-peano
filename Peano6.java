
public class Peano6
{
    public static Peano6 ZERO = new Peano6 ();
    public static Peano6 ONE = ZERO.S();
    public static Peano6 TWO = ONE.S();
    public static Peano6 THREE = TWO.S();
    public static Peano6 FOUR = THREE.S();
    public static Peano6 FIVE = FOUR.S();
    public static Peano6 SIX = FIVE.S();
    public static Peano6 SEVEN = SIX.S();
    public static Peano6 EIGHT = SEVEN.S();
    public static Peano6 NINE = EIGHT.S();
    public static Peano6 TEN = NINE.S();
    
    private Peano6 next = null;
    private final Peano6 prev;
    
    private Peano6 (Peano6 prev)
    {
        this.prev = prev;
    }

    private Peano6 ()
    {
        this (null);
    }

    public boolean isZero ()
    {
        return this == ZERO;
    }

    public Peano6 S ()
    {
        if (next == null) {
            next = new Peano6 (this);
        }
        return next;
    }

    public Peano6 D ()
    {
        if (isZero ()) throw new IllegalArgumentException ("D called on Zero");
        return prev;
    }
    
    public boolean lt (Peano6 other)
    {
        if (this == other) return false;
        Peano6 p = this;
        Peano6 q = other;
        while (true) {
            p = p.next;
            if (p == null) return false;
            if (p == other) return true;
            q = q.next;
            if (q == null) return true;
            if (q == this) return false;
        }
    }

    public boolean gt (Peano6 other)
    {
        return other.lt (this);
    }
    
    public boolean leq (Peano6 other)
    {
        return ! gt (other);
    }
    
    public boolean geq (Peano6 other)
    {
        return ! lt (other);
    }

    public Peano6 plus (Peano6 other)
    {
        Peano6 result = this;
        while (other != ZERO) {
            result = result.S ();
            other = other.D ();
        }
        return result;
    }

    public Peano6 minus (Peano6 other)
    {
        Peano6 result = this;
        while (other != ZERO) {
            result = result.D ();
            other = other.D ();
        }
        return result;
    }

    public Peano6 minus_special (Peano6 other)
    {
        Peano6 result = this;
        while (other != ZERO) {
            if (result == ZERO)
                return null;
            result = result.D ();
            other = other.D ();
        }
        return result;
    }

    public Peano6 mult (Peano6 other)
    {
        if (other == ZERO) return ZERO;
        Peano6 result = this;
        while (other != ONE) {
            result = result.plus (this);
            other = other.D ();
        }
        return result;
    }
    
    public Peano6 square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Peano6 quotient;
        final Peano6 remainder;
        
        public DivResult (Peano6 quotient, Peano6 remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Peano6 other)
    {
        Peano6 div = ZERO;
        Peano6 rem = this;
        while (rem.geq (other)) {
            rem = rem.minus (other);
            div = div.S ();
        }
        return new DivResult (div, rem);
    }

    public Peano6 div (Peano6 other)
    {
        return divrem (other).quotient;
    }

    public Peano6 rem (Peano6 other)
    {
        return divrem (other).remainder;
    }

    public boolean divisible (Peano6 other)
    {
        Peano6 rem = this;
        while (rem != ZERO) {
            rem = rem.minus_special (other);
            if (rem == null) return false;
        }
        return true;
    }

    private static String digitToString (Peano6 p)
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

    private static Peano6 charToDigit (char c)
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
    
    public static Peano6 parseInt (String s)
    {
        Peano6 result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 c = ONE; c.leq (N); c = c.S ())
            for (Peano6 b = ONE; b.lt (c); b = b.S ())
                for (Peano6 a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }
    
    public static void pythagorean2 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 c = ONE; c.leq (N); c = c.S ())
            for (Peano6 b = ONE; b != c; b = b.S ())
                for (Peano6 a = ONE; a != b; a = a.S ())
                    if (a.square().plus (b.square()) == c.square()) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void pythagorean3 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 c = ONE; c.leq (N); c = c.S ()) {
            Peano6 c_square = c.square ();
            for (Peano6 b = ONE; b != c; b = b.S ()) {
                Peano6 b_square = b.square ();
                for (Peano6 a = ONE; a != b; a = a.S ()) {
                    if (a.square().plus (b_square) == c_square) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void pythagorean4 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 c = ONE; c.leq (N); c = c.S ()) {
            Peano6 c_square = c.square ();
            for (Peano6 b = ONE; b != c; b = b.S ()) {
                Peano6 b_square = b.square ();
                Peano6 c_square_minus_b_square = c_square.minus (b_square);
                for (Peano6 a = ONE; a != b; a = a.S ()) {
                    if (a.square() == c_square_minus_b_square) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void pythagorean5 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 c = ONE, c_square = ONE; c.leq (N); c_square = c_square.plus (c).plus (c).S (), c = c.S ()) {
            for (Peano6 b = ONE, b_square = ONE; b != c; b_square = b_square.plus (b).plus (b).S (), b = b.S ()) {
                Peano6 c_square_minus_b_square = c_square.minus (b_square);
                for (Peano6 a = ONE, a_square = ONE; a != b; a_square = a_square.plus (a).plus (a).S (), a = a.S ()) {
                    if (a_square == c_square_minus_b_square) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void pythagorean6 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 c = ONE, c_square = ONE; c.leq (N); c_square = c_square.plus (c).plus (c).S (), c = c.S ()) {
            for (Peano6 b = ONE, c_square_minus_b_square = c.square ().D (); b != c;
                            c_square_minus_b_square = c_square_minus_b_square.minus (b).minus (b).D (), b = b.S ()) {
                for (Peano6 a = ONE, a_square = ONE; a != b; a_square = a_square.plus (a).plus (a).S (), a = a.S ()) {
                    if (a_square == c_square_minus_b_square) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void perfect (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 i = ONE; i.leq (N); i = i.S ()) {
            Peano6 sum = ZERO;
            for (Peano6 j = ONE; j.lt (i); j = j.S ())
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

    public static void perfect2 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 i = ONE; i.leq (N); i = i.S ()) {
            Peano6 sum = ZERO;
            for (Peano6 j = ONE; j != i; j = j.S ()) {
                if (i.rem (j) == ZERO)
                    sum = sum.plus (j);
            }
            if (i == sum) {
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
            }
        }
        System.out.println ("--- Total: ");
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }

    public static void perfect3 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 i = ONE; i.leq (N); i = i.S ()) {
            Peano6 sum = ZERO;
            for (Peano6 j = ONE; j != i; j = j.S ()) {
                if (i.divisible (j))
                    sum = sum.plus (j);
            }
            if (i == sum) {
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
            }
        }
        System.out.println ("--- Total: ");
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }
   
    public static void perfect4 (Peano6 N)
    {
        Debug.Timer.start ();
        for (Peano6 i = TWO; i.leq (N); i = i.S ()) {
            Peano6 sum = ZERO;
            Peano6 j = ONE;
            Peano6 j_times_2 = TWO;
            while (true) {
                if (i.divisible (j))
                    sum = sum.plus (j);
                if (j_times_2 == i) break;
                j_times_2 = j_times_2.S ();
                if (j_times_2 == i) break;
                j_times_2 = j_times_2.S ();
                j = j.S ();
            }
            if (i == sum) {
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
            }
        }
        System.out.println ("--- Total: ");
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }
    
    public static void main (String [] args)
    {
        pythagorean (Peano6.parseInt ("1000"));
        perfect (Peano6.parseInt ("10000"));
    }
}
