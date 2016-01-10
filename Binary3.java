public class Binary
{
    enum Digit
    {
        ZERO,
        ONE;
    }
    
    enum CompareResult
    {
        LT,
        EQU,
        GT
    }
    
    private final Digit lowest;
    private final Binary next;
    
    public static Binary ZERO = new Binary (Digit.ZERO);
    public static Binary ONE = new Binary (Digit.ONE);
    public static Binary TWO = new Binary (Digit.ZERO, ONE);
    public static Binary THREE = TWO.S();
    public static Binary FOUR = THREE.S();
    public static Binary FIVE = FOUR.S();
    public static Binary SIX = FIVE.S();
    public static Binary SEVEN = SIX.S();
    public static Binary EIGHT = SEVEN.S();
    public static Binary NINE = EIGHT.S();
    public static Binary TEN = NINE.S();

    
    private Binary (Digit d, Binary next)
    {
        this.lowest = d;
        this.next = next;
    }

    private Binary (Digit d)
    {
        this.lowest = d;
        this.next = null;
    }
    
    public boolean isZero ()
    {
        return lowest == Digit.ZERO && next == null;
    }
    
    private static CompareResult compare (Binary x, Binary y)
    {
        if (x == y)    return CompareResult.EQU;
        if (x == null) return CompareResult.LT;
        if (y == null) return CompareResult.GT;
        CompareResult r = compare (x.next, y.next);
        if (r != CompareResult.EQU) {
            return r;
        }
        return x.lowest == y.lowest ? CompareResult.EQU : x.lowest == Digit.ZERO ? CompareResult.LT : CompareResult.GT;
    }
    
    public boolean eq (Binary other)
    {
        if (this == other) return true;
        if (lowest != other.lowest) return false;
        if (next == null) return other.next == null;
        if (other.next == null) return false;
        return next.eq (other.next);
    }
    
    @Override
    public boolean equals (Object other)
    {
        return eq ((Binary) other);
    }

    public boolean neq (Binary other)
    {
        return ! eq (other);
    }

    public boolean lt (Binary other)
    {
        return compare (this, other) == CompareResult.LT;
    }
    
    public boolean leq (Binary other)
    {
        return compare (this, other) != CompareResult.GT;
    }

    public boolean geq (Binary other)
    {
        return compare (this, other) != CompareResult.LT;
    }

    public boolean gt (Binary other)
    {
        return compare (this, other) == CompareResult.GT;
    }
    
    public Binary S ()
    {
        if (lowest == Digit.ZERO) return new Binary (Digit.ONE, next);
        if (next == null) return TWO;
        return new Binary (Digit.ZERO, next.S ());
    }

    private Binary D0 ()
    {
        if (lowest == Digit.ONE)
            return next == null ? null : new Binary (Digit.ZERO, next);
        if (next == null)
            throw new IllegalArgumentException ("Negative number");
        return new Binary (Digit.ONE, next.D0());
    }

    public Binary D ()
    {
        Binary r = D0 ();
        return r == null ? ZERO : r;
    }
    
    private Binary add_carry (Binary x, Binary y)
    {
        if (x == null) {
            if (y == null) return ONE;
            return y.S ();
        }
        if (y == null) return x.S ();

        Digit d = x.lowest == y.lowest ? Digit.ONE : Digit.ZERO;
        Binary s = (x.lowest == Digit.ZERO && y.lowest == Digit.ZERO) ?
            add_no_carry (x.next, y.next) :
            add_carry (x.next, y.next);
        return new Binary (d, s);
    }
    
    private Binary add_no_carry (Binary x, Binary y)
    {
        if (x == null) return y;
        if (y == null) return x;

        Digit d = x.lowest == y.lowest ? Digit.ZERO : Digit.ONE;
        Binary s = (x.lowest == Digit.ONE && y.lowest == Digit.ONE) ?
            add_carry (x.next, y.next) :
            add_no_carry (x.next, y.next);
        return new Binary (d, s);
    }

    public Binary plus (Binary other)
    {
        return add_no_carry (this, other);
    }

    private static Binary sub_borrow (Binary x, Binary y)
    {
        if (x == null)
            throw new IllegalArgumentException ("Negative number");
        if (y == null) return x.D0 ();

        Digit d = x.lowest == y.lowest ? Digit.ONE : Digit.ZERO;
        Binary s = (x.lowest == Digit.ONE && y.lowest == Digit.ZERO) ?
            sub_no_borrow (x.next, y.next) :
            sub_borrow (x.next, y.next);
        return (s == null && d == Digit.ZERO) ? null : new Binary (d, s);
    }

    private static Binary sub_no_borrow (Binary x, Binary y)
    {
        if (y == null) return x;
        if (x == null) throw new IllegalArgumentException ("Negative number");

        Digit d = x.lowest == y.lowest ? Digit.ZERO : Digit.ONE;
        Binary s = (x.lowest == Digit.ZERO && y.lowest == Digit.ONE) ?
            sub_borrow (x.next, y.next) :
            sub_no_borrow (x.next, y.next);
        return (s == null && d == Digit.ZERO) ? null : new Binary (d, s);
    }
    
    public Binary minus (Binary other)
    {
        Binary result = sub_no_borrow (this, other);
        return result == null ? ZERO : result;
    }
    
    public Binary shift_left ()
    {
        return isZero() ? this : new Binary (Digit.ZERO, this);
    }

    public Binary shift_right ()
    {
        return next == null ? ZERO : next;
    }

    public Binary mult (Binary other)
    {
        if (other.isZero ()) return ZERO;
        Binary r = mult (other.shift_right ()).shift_left ();
        if (other.lowest == Digit.ONE) r = r.plus (this);
        return r;
    }
    
    public Binary square ()
    {
        return mult (this);
    }

    public static class DivResult
    {
        final Binary quotient;
        final Binary remainder;
        
        public DivResult (Binary quotient, Binary remainder)
        {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
    
    public DivResult divrem (Binary other)
    {
        if (this.lt (other)) {
            return new DivResult (ZERO, this);
        }
        DivResult dr = this.shift_right ().divrem (other);
        Binary r = dr.remainder.shift_left ();
        Binary q = dr.quotient.shift_left ();
        if (lowest == Digit.ONE) {
            r = r.S ();
        }
        if (r.geq (other)) {
            r = r.minus (other);
            q = q.S ();
        }
        return new DivResult (q, r);
    }
    
    public Binary div (Binary other)
    {
        return divrem (other).quotient;
    }
    
    public Binary rem (Binary other)
    {
        return divrem (other).remainder;
    }
    
    private static String digitToString (Binary p)
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

    private static Binary charToDigit (char c)
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
    
    public static Binary parseInt (String s)
    {
        Binary result = ZERO;
        for (char c : s.toCharArray ()) {
            result = result.mult (TEN).plus (charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; c.leq (N); c = c.S ())
            for (Binary b = ONE; b.lt (c); b = b.S ())
                for (Binary a = ONE; a.lt (b); a = a.S ())
                    if (a.square().plus (b.square()).equals (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void pythagorean2 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; c.leq (N); c = c.S ())
            for (Binary b = ONE; b.neq (c); b = b.S ())
                for (Binary a = ONE; a.neq (b); a = a.S ())
                    if (a.square().plus (b.square()).eq (c.square())) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }
    
    public static void perfect (Binary N)
    {
        Debug.Timer.start ();
        for (Binary i = ONE; i.leq (N); i = i.S ()) {
            Binary sum = ZERO;
            for (Binary j = ONE; j.lt (i); j = j.S ()) {
                if (i.rem (j).isZero ())
                    sum = sum.plus (j);
            }
            if (i.equals (sum)) {
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
            }
        }
        System.out.println ("--- Total: ");
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }

    public static void perfect2 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary i = ONE; i.leq (N); i = i.S ()) {
            Binary sum = ZERO;
            for (Binary j = ONE; j.neq (i); j = j.S ()) {
                if (i.rem (j).isZero ())
                    sum = sum.plus (j);
            }
            if (i.eq (sum)) {
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
        pythagorean2 (Binary.parseInt ("1000"));
//        perfect2 (Binary.parseInt ("10000"));
    }
}
