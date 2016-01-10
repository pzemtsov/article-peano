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
    
    public static Binary ZERO = null;
    public static Binary ONE = new Binary (Digit.ONE);
    public static Binary TWO = newBinary (Digit.ZERO, ONE);
    public static Binary THREE = S (TWO);
    public static Binary FOUR = S (THREE);
    public static Binary FIVE = S (FOUR);
    public static Binary SIX = S (FIVE);
    public static Binary SEVEN = S (SIX);
    public static Binary EIGHT = S (SEVEN);
    public static Binary NINE = S (EIGHT);
    public static Binary TEN = S (NINE);

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

    public static boolean isZero (Binary x)
    {
        return x == ZERO;
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
    
    public static boolean eq (Binary x, Binary y)
    {
        return x == y;
    }
    
    public static boolean neq (Binary x, Binary y)
    {
        return x != y;
    }

    public static boolean lt (Binary x, Binary y)
    {
        return compare (x, y) == CompareResult.LT;
    }
    
    public static boolean leq (Binary x, Binary y)
    {
        return compare (x, y) != CompareResult.GT;
    }

    public static boolean geq (Binary x, Binary y)
    {
        return compare (x, y) != CompareResult.LT;
    }

    public static boolean gt (Binary x, Binary y)
    {
        return compare (x, y) == CompareResult.GT;
    }
    
    public static Binary S (Binary x)
    {
        if (x == null) return ONE;
        if (x.s == null) {
            x.s = x.lowest == Digit.ZERO ? newBinary (Digit.ONE, x.next) :
                  x.next == null ? TWO : newBinary (Digit.ZERO, S (x.next));
        }
        return x.s;
    }

    private static Binary D (Binary x)
    {
        if (x == null)
            throw new IllegalArgumentException ("Negative number");
        if (x == ONE)
            return null;
        if (x.lowest == Digit.ONE)
            return newBinary (Digit.ZERO, x.next);
        return newBinary (Digit.ONE, D(x.next));
    }

    private static Binary add_carry (Binary x, Binary y)
    {
        if (x == null) {
            if (y == null) return ONE;
            return S (y);
        }
        if (y == null) return S (x);

        Digit d = x.lowest == y.lowest ? Digit.ONE : Digit.ZERO;
        Binary s = (x.lowest == Digit.ZERO && y.lowest == Digit.ZERO) ?
            add_no_carry (x.next, y.next) :
            add_carry (x.next, y.next);
        return newBinary (d, s);
    }
    
    private static Binary add_no_carry (Binary x, Binary y)
    {
        if (x == null) return y;
        if (y == null) return x;

        Digit d = x.lowest == y.lowest ? Digit.ZERO : Digit.ONE;
        Binary s = (x.lowest == Digit.ONE && y.lowest == Digit.ONE) ?
            add_carry (x.next, y.next) :
            add_no_carry (x.next, y.next);
        return newBinary (d, s);
    }

    public static Binary plus (Binary x, Binary y)
    {
        return add_no_carry (x, y);
    }

    private static Binary sub_borrow (Binary x, Binary y)
    {
        if (x == null)
            throw new IllegalArgumentException ("Negative number");
        if (y == null) return D (x);

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
    
    public static Binary minus (Binary x, Binary y)
    {
        return sub_no_borrow (x, y);
    }
    
    public static Binary shift_left (Binary x)
    {
        return isZero(x) ? x : newBinary (Digit.ZERO, x);
    }

    public static Binary shift_right (Binary x)
    {
        return x.next;
    }

    public static Binary mult (Binary x, Binary y)
    {
        if (isZero (y)) return ZERO;
        Binary r = shift_left (mult (x, shift_right (y)));
        if (y.lowest == Digit.ONE) r = plus (r, x);
        return r;
    }
    
    public static Binary square (Binary x)
    {
        return mult (x, x);
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
    
    public static DivResult divrem (Binary x, Binary y)
    {
        if (lt (x, y)) {
            return new DivResult (ZERO, x);
        }
        DivResult dr = divrem (shift_right (x), y);
        Binary r = shift_left (dr.remainder);
        Binary q = shift_left (dr.quotient);
        if (x.lowest == Digit.ONE) {
            r = S (r);
        }
        if (geq (r, y)) {
            r = minus (r, y);
            q = S (q);
        }
        return new DivResult (q, r);
    }
    
    public static Binary div (Binary x, Binary y)
    {
        return divrem (x, y).quotient;
    }
    
    public static Binary rem (Binary x, Binary y)
    {
        if (lt (x, y)) {
            return x;
        }
        Binary r = shift_left (rem (shift_right (x), y));
        if (x.lowest == Digit.ONE) {
            r = S (r);
        }
        if (geq (r, y)) {
            r = minus (r, y);
        }
        return r;
    }
    
    private static String digitToString (Binary p)
    {
        if (eq (p, ZERO))  return "0";
        if (eq (p, ONE))   return "1";
        if (eq (p, TWO))   return "2";
        if (eq (p, THREE)) return "3";
        if (eq (p, FOUR))  return "4";
        if (eq (p, FIVE))  return "5";
        if (eq (p, SIX))   return "6";
        if (eq (p, SEVEN)) return "7";
        if (eq (p, EIGHT)) return "8";
        if (eq (p, NINE))  return "9";
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
    
    public static String toString (Binary x)
    {
        DivResult d = divrem (x, TEN);
        String digit = digitToString (d.remainder);
        return isZero (d.quotient) ? digit : toString (d.quotient) + digit;
    }
    
    public static Binary parseInt (String s)
    {
        Binary result = ZERO;
        for (char c : s.toCharArray ()) {
            result = plus (mult (result, TEN), charToDigit (c));
        }
        return result;
    }
    
    public static void pythagorean (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; leq (c, N); c = S (c))
            for (Binary b = ONE; lt (b, c); b = S (b))
                for (Binary a = ONE; lt (a, b); a = S (a))
                    if (eq (plus (square(a), square(b)), square(c))) {
                        System.out.println (toString (a) + " " + toString (b) + " " + toString (c));
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }

    public static void pythagorean2 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; leq (c, N); c = S (c))
            for (Binary b = ONE; neq (b, c); b = S (b))
                for (Binary a = ONE; neq (a, b); a = S (a))
                    if (eq (plus (square(a), square(b)), square(c))) {
                        System.out.println (toString (a) + " " + toString (b) + " " + toString (c));
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
    }
    
    public static void pythagorean3 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE; leq (c, N); c = S (c)) {
            Binary c_square = square (c);
            for (Binary b = ONE; neq (b, c); b = S (b)) {
                Binary b_square = square (b);
                for (Binary a = ONE; neq (a, b); a = S (a)) {
                    if (eq (plus (square(a), b_square), c_square)) {
                        System.out.println (toString (a) + " " + toString (b) + " " + toString (c));
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
        for (Binary c = ONE; leq (c, N); c = S (c)) {
            Binary c_square = square (c);
            for (Binary b = ONE; neq (b, c); b = S (b)) {
                Binary b_square = square (b);
                Binary c_square_minus_b_square = minus (c_square, b_square);
                for (Binary a = ONE; neq (a, b); a = S (a)) {
                    if (square(a).equals (c_square_minus_b_square)) {
                        System.out.println (toString (a) + " " + toString (b) + " " + toString (c));
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
        for (Binary c = ONE, c_square = ONE; leq (c, N); c_square = S (plus (plus (c_square, c), c)), c = S (c)) {
            for (Binary b = ONE, b_square = ONE; neq (b, c); b_square = S (plus (plus (b_square, b), b)), b = S (b)) {
                Binary c_square_minus_b_square = minus (c_square, b_square);
                for (Binary a = ONE, a_square = ONE; neq (a, b); a_square = S (plus (plus (a_square, a), a)), a = S (a)) {
                    if (a_square == c_square_minus_b_square) {
                        System.out.println (toString (a) + " " + toString (b) + " " + toString (c));
                        Debug.Timer.stop ();
                        Debug.Timer.dump ();
                    }
                }
            }
        }
    }

    public static void pythagorean6 (Binary N)
    {
        Debug.Timer.start ();
        for (Binary c = ONE, c_square = ONE; leq (c, N); c_square = S (plus (plus (c_square, c), c)), c = S (c)) {
            for (Binary b = ONE, c_square_minus_b_square = D (square (c)); neq (b, c);
                            c_square_minus_b_square = D (minus (minus (c_square_minus_b_square, b), b)), b = S (b)) {
                for (Binary a = ONE, a_square = ONE; neq (a, b); a_square = S (plus (plus (a_square, a), a)), a = S (a)) {
                    if (a_square == c_square_minus_b_square) {
                        System.out.println (toString (a) + " " + toString (b) + " " + toString (c));
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
        for (Binary i = ONE; leq (i, N); i = S (i)) {
            Binary sum = ZERO;
            for (Binary j = ONE; lt (j, i); j = S (j)) {
                if (isZero (rem (i, j)))
                    sum = plus (sum, j);
            }
            if (eq (i, sum)) {
                System.out.println (toString (i));
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
        for (Binary i = ONE; leq (i, N); i = S (i)) {
            Binary sum = ZERO;
            for (Binary j = ONE; neq (j, i); j = S (j)) {
                if (isZero (rem (i, j)))
                    sum = plus (sum, j);
            }
            if (eq (i, sum)) {
                System.out.println (toString (i));
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
        for (Binary i = ONE; leq (i, N); i = S (i)) {
            Binary sum = ZERO;
            Binary k = S (shift_right (i));
            for (Binary j = ONE; neq (j, k); j = S (j)) {
                if (isZero (rem (i, j)))
                    sum = plus (sum, j);
            }
            if (eq (i, sum)) {
                System.out.println (toString (i));
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
        pythagorean5 (Binary.parseInt ("1000"));
//        perfect4 (Binary.parseInt ("10000"));
    }
}
