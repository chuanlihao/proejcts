package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


interface Monadic<A> { }

interface Monad<A, MA extends Monadic<A>, B, MB extends Monadic<B>> {
  MA ret(A a);
  MB bind(MA ma, Function<A, MB> f);
}

class MonadicHelper<A, MA extends Monadic<A>> {

  MA value;

  static <A, MA extends Monadic<A>> MonadicHelper<A, MA> from(Monad<A, MA, ?, ?> monad, A value) {
    return new MonadicHelper<>(monad.ret(value));
  }

  <B, MB extends Monadic<B>> MonadicHelper<B, MB> bind(Monad<A, MA, B, MB> monad, Function<A, MB> f) {
    return new MonadicHelper<>(monad.bind(value, f));
  }

  private MonadicHelper(MA value) {
    this.value = value;
  }

  MA get() {
    return value;
  }
}


class Maybe<T> implements Monadic<T> {

  static <T> Maybe<T> nothing() {
    return new Maybe<>(null);
  }

  static <T> Maybe<T> just(T value) {
    return new Maybe<>(value);
  }

  private T value;

  private Maybe(T value) {
    this.value = value;
  }

  boolean hasValue() {
    return value != null;
  }

  T getValue() {
    return value;
  }
}

class MaybeMonad<A, B> implements Monad<A, Maybe<A>, B, Maybe<B>> {
  @Override
  public Maybe<A> ret(A a) {
    return Maybe.just(a);
  }

  @Override
  public Maybe<B> bind(Maybe<A> ma, Function<A, Maybe<B>> f) {
    return ma.hasValue()
        ? f.apply(ma.getValue())
        : Maybe.nothing();
  }
}


class MonadicList<T> implements Monadic<T> {
  private List<T> values;

  public MonadicList(List<T> values) {
    this.values = values;
  }

  List<T> getValues() {
    return values;
  }

  void setValues(List<T> values) {
    this.values = values;
  }
}

class ListMonad<A, B> implements Monad<A, MonadicList<A>, B, MonadicList<B>> {

  @Override
  public MonadicList<A> ret(A a) {
    return new MonadicList<>(Arrays.asList(a));
  }

  @Override
  public MonadicList<B> bind(MonadicList<A> ma, Function<A, MonadicList<B>> f) {
    List<B> result = new ArrayList<>();
    for (A a : ma.getValues()) {
      result.addAll(f.apply(a).getValues());
    }
    return new MonadicList<>(result);
  }
}


public class TestMonad {
  public static void main(String[] args) {
    Function<Integer, Maybe<Integer>> next =
        x -> x < 5 ? Maybe.just(x + x) : Maybe.nothing();
    MaybeMonad<Integer, Integer> maybe = new MaybeMonad<>();
    printMaybe(MonadicHelper.from(maybe, 2).bind(maybe, next).get());
    printMaybe(MonadicHelper.from(maybe, 2).bind(maybe, next).bind(maybe, next).get());
    printMaybe(
        MonadicHelper.from(maybe, 2).bind(maybe, next).bind(maybe, next).bind(maybe, next).get());

    Function<Integer, MonadicList<Integer>> oneOrTwo =
        x -> new MonadicList<>(Arrays.asList(x, x + x));
    ListMonad<Integer, Integer> list = new ListMonad<>();
    printMonadicList(MonadicHelper.from(list, 1).bind(list, oneOrTwo).get());
    printMonadicList(MonadicHelper.from(list, 1).bind(list, oneOrTwo).bind(list, oneOrTwo).get());
    printMonadicList(
        MonadicHelper
            .from(list, 1)
            .bind(list, oneOrTwo)
            .bind(list, oneOrTwo)
            .bind(list, oneOrTwo)
            .get());
  }

  static <T> void printMaybe(Maybe<T> mt) {
    System.out.println(mt.hasValue() ? String.valueOf(mt.getValue()) : "Nothing");
  }

  static <T> void printMonadicList(MonadicList<T> mt) {
    System.out.println(mt.getValues());
  }
}
