package moe.caramel.mica.natives;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import moe.caramel.mica.Mica;
import net.minecraft.Util;

public interface NtDll extends Library {

    /* NTDLL */
    NtDll INSTANCE = Native.load("ntdll", NtDll.class);

    void RtlGetNtVersionNumbers(
        IntByReference MajorVersion,
        IntByReference MinorVersion,
        IntByReference BuildNumber
    );

    static void getBuildNumber() {
        // Check OS
        if (Util.getPlatform() != Util.OS.WINDOWS) {
            return;
        }

        // Get Windows Info
        final IntByReference majorVersion = new IntByReference();
        final IntByReference buildNumber = new IntByReference();
        INSTANCE.RtlGetNtVersionNumbers(majorVersion, new IntByReference(), buildNumber);

        // Write Info
        Mica.majorVersion = majorVersion.getValue();
        Mica.buildNumber = (buildNumber.getValue() & ~0xF0000000);
    }
}
