package scala.scalanative.unsafe

import scala.scalanative.runtime.{Intrinsics, fromRawPtr, toRawPtr, libc}
import scala.scalanative.unsigned._

private[scalanative] trait UnsafePackageCompat {
  private[scalanative] given reflect.ClassTag[Array[?]] =
    reflect.classTag[Array[AnyRef]].asInstanceOf[reflect.ClassTag[Array[?]]]

  /** Heap allocate and zero-initialize n values using current implicit
   *  allocator.
   */
  inline def alloc[T](
      inline n: CSize = 1.toUSize
  )(using tag: Tag[T], zone: Zone): Ptr[T] = {
    val size = sizeof[T] * n
    val ptr = zone.alloc(size)
    val rawPtr = toRawPtr(ptr)
    libc.memset(rawPtr, 0, size)
    ptr.asInstanceOf[Ptr[T]]
  }

  /** Heap allocate and zero-initialize n values using current implicit
   *  allocator. This method takes argument of type `CSSize` for easier interop,
   *  but it' always converted into `CSize`
   */
  @deprecated(
    "alloc with signed type is deprecated, convert size to unsigned value",
    since = "0.4.0"
  )
  inline def alloc[T](inline n: CSSize)(using Tag[T], Zone): Ptr[T] =
    alloc[T](n.toUInt)

  /** Stack allocate n values of given type */
  inline def stackalloc[T](
      inline n: CSize = 1.toUSize
  )(using Tag[T]): Ptr[T] = {
    val size = sizeof[T] * n
    val rawPtr = Intrinsics.stackalloc(size)
    libc.memset(rawPtr, 0, size)
    fromRawPtr[T](rawPtr)
  }

  /** Stack allocate n values of given type.
   *
   *  Note: unlike alloc, the memory is not zero-initialized. This method takes
   *  argument of type `CSSize` for easier interop, but it's always converted
   *  into `CSize`
   */
  @deprecated(
    "alloc with signed type is deprecated, convert size to unsigned value",
    since = "0.4.0"
  )
  inline def stackalloc[T](inline n: CSSize)(using Tag[T]): Ptr[T] =
    stackalloc[T](n.toUSize)
}
