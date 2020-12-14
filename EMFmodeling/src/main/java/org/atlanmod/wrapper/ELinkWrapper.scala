package org.atlanmod.wrapper

import org.atlanmod.ELink

class ELinkWrapper (l: ELink) extends WrapperSerializable[ELink] {
    def unwrap: ELink = l
}
