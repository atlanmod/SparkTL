package org.atlanmod.tl.util

object ListUtils {

    trait Weakable {
        def weak_equals(o: Any) : Boolean
    }

    def weak_eqList[A <: Weakable, B <: Weakable](lA: List[A], lB: List[B]): Boolean = {
        for(a <- lA)
            if (!(lB.count(v => v.weak_equals(a)) == lA.count(v => v.weak_equals(a)))) return false
        for(b <- lB)
            if (!(lB.count(v => v.weak_equals(b)) == lA.count(v => v.weak_equals(b)))) return false
        true
    }

    def eqList[A, B](lA: List[A], lB: List[B]): Boolean = {
        for(a <- lA)
            if (!(lB.count(v => v.equals(a)) == lA.count(v => v.equals(a)))) {
                if(lA.size == 9)
                    return false
                return false
            }
        for(b <- lB)
            if (!(lB.count(v => v.equals(b)) == lA.count(v => v.equals(b)))) {
                if(lB.size == 9)
                    return false
                return false
            }

        true
    }


    private def option_map[A, B](f: A => B, o: Option[A]) : Option[B]  = {
        o match {
            case Some(a) =>Some(f(a))
            case None => None
        }
    }

    def listToListList[A](l: List[A]): List[List[A]] = {
        l.map(e => List(e))
    }

    def hasLength[A](l: List[A], n: Int): Boolean = {
        l.length == n
    }

    def optionToList[A](o: Option[A]) : List[A] = {
        o match {
            case Some(a) => List(a)
            case None => List()
        }
    }

    def optionListToList[A](o: Option[List[A]]) : List[A] = {
        o match {
            case Some(a) => a
            case None => List()
        }
    }

    def optionList2List[A](l: List[Option[A]]) : List[A] = {
        l.flatMap(v => optionToList(v))
    }

    def singleton[A](a: A) : List[A] = {
        List(a)
    }

    def maybeSingleton[A](a: Option[A]): Option[List[A]] = {
        option_map((v: A) => singleton(v), a)
    }

    def singletons[A](l: List[A]) : List[List[A]] = {
        listToListList(l)
    }

    def maybeSingletons[A](a: Option[List[A]]): Option[List[List[A]]] = {
        option_map((v: List[A]) => singletons(v), a)
    }

    def mapWithIndex[A, B](f: (Int, A) => B, n: Int, l: List[A]): List[B] = {
        l match {
            case List() => List()
            case a :: t => f(n, a) :: mapWithIndex(f, n+1, t)
        }
    }

    def zipWith[A, B, C](f: (A, B) => C, la: List[A], lb: List[B]) : List[C] = {
        (la, lb) match {
            case (ea::eas, eb::ebs) => f(ea, eb) :: zipWith(f, eas, ebs)
            case (List(), _) => List()
            case (_, List()) => List()
        }
    }

    def sum_list_option[A] (a1 : Option[List[A]], a2 : Option[List[A]]): Option[List[A]] = {
        (a1, a2) match {
            case (Some(l1), Some(l2)) => Some (l1 ++ l2)
            case (Some(l1), None) => Some (l1)
            case (None, Some(l2)) => Some (l2)
            case _ => None
        }
    }

}
