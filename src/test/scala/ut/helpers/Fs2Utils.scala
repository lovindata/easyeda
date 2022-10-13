package com.ilovedatajjia
package ut.helpers

import cats.effect.IO
import fs2._
import fs2.io.file._

/**
 * Static function for building [[fs2]] objects.
 */
object Fs2Utils {

  /**
   * Read simulated [[Stream]] representation.
   * @param path
   *   The [[Stream]] resource path
   * @param nbChunks
   *   Simulated number of chunks in the stream
   * @return
   *   Simulated [[Stream]]
   */
  def fromResourceStream(path: String, nbChunks: Int): IO[Stream[IO, Byte]] = for {
    stringRep     <-
      Files[IO].readAll(Path(getClass.getResource(path).getPath)).through(text.utf8.decode).compile.string
    stringSplitRep = stringRep.sliding(stringRep.length / nbChunks).toArray
    streamRep      = Stream.emits(stringSplitRep).through(text.utf8.encode)
  } yield streamRep

}
