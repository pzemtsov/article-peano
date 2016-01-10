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
    private Binary link0 = null;
    private Binary link1 = null;
    private Binary s = null;
    
    public static Binary ZERO = new Binary (Digit.ZERO);
    public static Binary ONE = new Binary (Digit.ONE);
    public static Binary TWO = newBinary (Digit.ZERO, ONE);
    public static Binary THREE = TWO.S();
    public static Binary FOUR = THREE.S();
    public static Binary FIVE = FOUR.S();
    public static Binary SIX = FIVE.S();
    public static Binary SEVEN = SIX.S();
    public static Binary EIGHT = SEVEN.S();
    public static Binary NINE = EIGHT.S();
    public static Binary TEN = NINE.S();

    
    static int count = 0;
    static Binary max = null;
    
    private Binary (Digit d, Binary next)
    {
        this.lowest = d;
        this.next = next;
        ++ count;
        if (max == null || this.gt (max)) max = this;
    }

    private Binary (Digit d)
    {
        this.lowest = d;
        this.next = null;
        ++ count;
        if (max == null || this.gt (max)) max = this;
    }

    private static Binary newBinary (Digit d, Binary next)
    {
        if (next != null) {
            if (d == Digit.ONE) {
                if (next.link1 == null) {
                    next.link1 = new Binary (d, next);
                }
                return next.link1;
            } else {
                if (next.link0 == null) {
                    next.link0 = new Binary (d, next);
                }
                return next.link0;
            }
        }
        return d == Digit.ONE ? ONE : ZERO;
    }

    public boolean isZero ()
    {
        return this == ZERO;
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
        return this == other;
    }
    
    @Override
    public boolean equals (Object other)
    {
        return this == other;
    }

    public boolean neq (Binary other)
    {
        return this != other;
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
        if (s == null) {
            s = lowest == Digit.ZERO ? newBinary (Digit.ONE, next) :
                  next == null ? TWO : newBinary (Digit.ZERO, next.S ());
        }
        return s;
    }

    private Binary D0 ()
    {
        if (lowest == Digit.ONE)
            return next == null ? null : newBinary (Digit.ZERO, next);
        if (next == null)
            throw new IllegalArgumentException ("Negative number");
        return newBinary (Digit.ONE, next.D0());
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
        return newBinary (d, s);
    }
    
    private Binary add_no_carry (Binary x, Binary y)
    {
        if (x == null) return y;
        if (y == null) return x;

        Digit d = x.lowest == y.lowest ? Digit.ZERO : Digit.ONE;
        Binary s = (x.lowest == Digit.ONE && y.lowest == Digit.ONE) ?
            add_carry (x.next, y.next) :
            add_no_carry (x.next, y.next);
        return newBinary (d, s);
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
        return (s == null && d == Digit.ZERO) ? null : newBinary (d, s);
    }

    private static Binary sub_no_borrow (Binary x, Binary y)
    {
        if (y == null) return x;
        if (x == null) throw new IllegalArgumentException ("Negative number");

        Digit d = x.lowest == y.lowest ? Digit.ZERO : Digit.ONE;
        Binary s = (x.lowest == Digit.ZERO && y.lowest == Digit.ONE) ?
            sub_borrow (x.next, y.next) :
            sub_no_borrow (x.next, y.next);
        return (s == null && d == Digit.ZERO) ? null : newBinary (d, s);
    }
    
    public Binary minus (Binary other)
    {
        Binary result = sub_no_borrow (this, other);
        return result == null ? ZERO : result;
    }
    
    public Binary shift_left ()
    {
        return isZero() ? this : newBinary (Digit.ZERO, this);
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
        if (this.lt (other)) {
            return this;
        }
        Binary r = this.shift_right ().rem (other).shift_left ();
        if (lowest == Digit.ONE) {
            r = r.S ();
        }
        if (r.geq (other)) {
            r = r.minus (other);
        }
        return r;
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
    
    public static void pythagorean3 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; c.leq (N); c = c.S ()) {
            Binary c_square = c.square ();
            for (Binary b = ONE; b.neq (c); b = b.S ()) {
                Binary b_square = b.square ();
                for (Binary a = ONE; a.neq (b); a = a.S ()) {
                    if (a.square().plus (b_square).equals (c_square)) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void pythagorean4 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; c.leq (N); c = c.S ()) {
            Binary c_square = c.square ();
            for (Binary b = ONE; b.neq (c); b = b.S ()) {
                Binary b_square = b.square ();
                Binary c_square_minus_b_square = c_square.minus (b_square);
                for (Binary a = ONE; a.neq (b); a = a.S ()) {
                    if (a.square().equals (c_square_minus_b_square)) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void pythagorean5 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE, c_square = ONE; c.leq (N); c_square = c_square.plus (c).plus (c).S (), c = c.S ()) {
            for (Binary b = ONE, b_square = ONE; b.neq (c); b_square = b_square.plus (b).plus (b).S (), b = b.S ()) {
                Binary c_square_minus_b_square = c_square.minus (b_square);
                for (Binary a = ONE, a_square = ONE; a.neq (b); a_square = a_square.plus (a).plus (a).S (), a = a.S ()) {
                    if (a_square == c_square_minus_b_square) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                        System.out.println (count + " " + max);
                    }
                }
            }
        }
    }

    public static void pythagorean6 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE, c_square = ONE; c.leq (N); c_square = c_square.plus (c).plus (c).S (), c = c.S ()) {
            for (Binary b = ONE, c_square_minus_b_square = c.square ().D (); b.neq (c);
                            c_square_minus_b_square = c_square_minus_b_square.minus (b).minus (b).D (), b = b.S ()) {
                for (Binary a = ONE, a_square = ONE; a.neq (b); a_square = a_square.plus (a).plus (a).S (), a = a.S ()) {
                    if (a_square == c_square_minus_b_square) {
                        System.out.println (a + " " + b + " " + c);
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
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

    public static void perfect4 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary i = ONE; i.leq (N); i = i.S ()) {
            Binary sum = ZERO;
            Binary k = i.shift_right ().S ();
            for (Binary j = ONE; j.neq (k); j = j.S ()) {
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
        System.out.println (count + " " + max);
    }

    public static void main (String [] args)
    {
//        pythagorean5 (Binary.parseInt ("1000"));
        perfect4 (Binary.parseInt ("10000"));
    }
}
