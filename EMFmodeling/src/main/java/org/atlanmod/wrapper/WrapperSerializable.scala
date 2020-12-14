package org.atlanmod.wrapper

trait WrapperSerializable[A] extends Serializable {
    def unwrap: A
}
