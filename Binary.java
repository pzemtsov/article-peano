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

    
    private Binary (Digit lowest, Binary next)
    {
        this.lowest = lowest;
        this.next = next;
    }

    private Binary (Digit lowest)
    {
        this.lowest = lowest;
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
        return compare (this, other) == CompareResult.EQU;
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
        Binary result = ZERO;
        Binary x = this;
        if (! x.isZero ()) {
            for (Binary y = other; y != null; y = y.next) {
                if (y.lowest == Digit.ONE) {
                    result = result.plus (x);
                }
                x = x.shift_left ();
            }
        }
        return result;
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
        Binary a = this;
        Binary b = other;
        Binary digit = ONE;
        while (a.gt (b)) {
            b = b.shift_left ();
            digit = digit.shift_left ();
        }
        Binary result = ZERO;
        while (true) {
            if (a.lt (other)) {
                return new DivResult (result, a);
            }
            if (a.geq (b)) {
                a = a.minus (b);
                result = result.plus (digit);
            }
            b = b.shift_right ();
            digit = digit.shift_right ();
        }
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
                System.out.println ("Found");
                System.out.println (i);
                Debug.Timer.stop ();
                Debug.Timer.dump ();
                System.out.println ("---");
            }
        }
        System.out.println ("--- Total: ");
        Debug.Timer.stop ();
        Debug.Timer.dump ();
    }
    
    public static void main (String [] args)
    {
        pythagorean (Binary.parseInt ("1000"));
        perfect (Binary.parseInt ("10000"));
    }
}
