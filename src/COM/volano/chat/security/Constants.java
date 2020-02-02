package COM.volano.chat.security;

public interface Constants {
    // 6D0FCD84CAA19AE09041B38251E66C59
    // 15665708D96147D824C654B4082DAB8A
    // 82D8F74B3921DBD6BC7D5A9C148F3484
    // A538AE54185DE01C58619E150AB5B542
    // 2521B1DB9992E5EF0AF061DBC5C2B6FD
    // A7E0C1CBC4824737D8556A4179BEB868
    // 4F55BDC3BE41C70EE59C7D8B08D5D9F2
    // 857C609A398515FBD24C1F60940F89C0
    // FBBA5FE12DE61F81611C6D47554E47F2
    // EF205C7AC80984D45B99E2C1AB9CFC9C
    // AD2E32D7A2E5579FB4EAD43D763246B1
    // 57DDD19C3493B57CEC10E66BA5B69290
    // E8EF873C8A666C43B748C12C0D9727AE
    // 76D09C1F373C2046DF7368737B8983E0
    // B77DAD248A323B0569AC74090C8A3FD2
    // C54C6721FE41A5B4F6652A94BBEAD0C1
    // 25839796F668785E6CC1639C3A48A4BA

    // New random build suffix.
    String BUILD_SUFFIX = "CBAF2AC7";

    // New random bytes for next time.
    byte[] NEW_BYTES = {-89, -68, 49, -103, 53, -1, -21, 49, 125, -119, -95, 45, -8, 115, -21, 83, -103, 113, -44, 73, 38, -7, 22, 14, 89, -114, 36, 11, -46, 54, -119, 35, 66, 98, -52, -39, -40, -127, 57, 125, 25, -109, -6, -79, -6, -124, -31, -57, 18, 73, -86, 100, 44, 120, -106, 38, -16, -21, -11, 0, -16, 16, 118, -95, 79, 37, -90, -16, -69, -121, -124, 99, 112, -121, -116, 19, -82, -63, 94, -25, 57, 10, 50, 88, 45, -77, 58, -58, 79, 76, -90, -56, 29, -73, 21, -34, 110, 60, -56, -99};

    // Random bytes used for keys below.
    byte[] BYTES = {-89, -46, 2, -119, -118, -88, -94, -103, 123, -35, -108, -108, -67, 89, -57, 37, -59, -88, 20, 108, -124, 97, 73, 94, -83, 12, 116, -50, -104, 7, -50, 59, -30, 69, -109, -35, 77, 56, 127, 78, 42, 110, 40, 73, 2, -54, -97, 3, 4, -54, 1, 95, 107, -115, 126, 96, -11, -48, 59, -15, 60, -31, 22, 105, 114, -78, 25, -7, -15, -56, -35, 94, -116, -17, -64, 62, -55, 63, -48, -104, -65, 1, 72, 93, 53, -4, 124, -8, -108, 1, -71, -90, 65, -27, 115, 4, 6, 100, -122, 77};

    // DSA public and private key parameters.
    String Y = "8218509436561656082743786779368239031372747937327111849584975957261124622533190289867614569592675233750610184280328239638479981829981519789409982641478773";
    String X = "510240016354089819288503354340181866274958739037";
    String P = "13232376895198612407547930718267435757728527029623408872245156039757713029036368719146452186041204237350521785240337048752071462798273003935646236777459223";
    String Q = "857393771208094202104259627990318636601332086981";
    String G = "5421644057436475141609648488325705128047428394380474376834667300766108262613900542681289080713724597310673074119355136085795982097390670890367185141189796";

    // Serialized Java version 1.8.0_242 sun.security.provider.DSAPublicKeyImpl.
    String PUBLIC_KEY = 
        "ACED0005737200146A6176612E73656375726974792E4B6579526570BDF94FB3" +
        "889AA5430200044C0009616C676F726974686D7400124C6A6176612F6C616E67" +
        "2F537472696E673B5B0007656E636F6465647400025B424C0006666F726D6174" +
        "71007E00014C00047479706574001B4C6A6176612F73656375726974792F4B65" +
        "7952657024547970653B7870740003445341757200025B42ACF317F8060854E0" +
        "0200007870000000F43081F13081A806072A8648CE38040130819C024100FCA6" +
        "82CE8E12CABA26EFCCF7110E526DB078B05EDECBCD1EB4A208F3AE1617AE01F3" +
        "5B91A47E6DF63413C5E12ED0899BCD132ACD50D99151BDC43EE737592E170215" +
        "00962EDDCC369CBA8EBB260EE6B6A126D9346E38C50240678471B27A9CF44EE9" +
        "1A49C5147DB1A9AAF244F05A434D6486931D2D14271B9E35030B71FD73DA1790" +
        "69B32E2935630E1C2062354D0DA20A6C416E50BE794CA40344000241009CEB3C" +
        "C1AFD624B5FB104B8AEAC1AC7D0C1415116970F28E2113C4F0BF30DC5ACC3C23" +
        "0CF33C1E0796EFD90D1DC899BD45A7312FA96BEBC8FE27A0B9F95A1C75740005" +
        "582E3530397E7200196A6176612E73656375726974792E4B6579526570245479" +
        "706500000000000000001200007872000E6A6176612E6C616E672E456E756D00" +
        "0000000000000012000078707400065055424C4943";

    // Serialized Java version 1.8.0_242 sun.security.provider.DSAPrivateKey.
    String PRIVATE_KEY = 
        "ACED0005737200146A6176612E73656375726974792E4B6579526570BDF94FB3" +
        "889AA5430200044C0009616C676F726974686D7400124C6A6176612F6C616E67" +
        "2F537472696E673B5B0007656E636F6465647400025B424C0006666F726D6174" +
        "71007E00014C00047479706574001B4C6A6176612F73656375726974792F4B65" +
        "7952657024547970653B7870740003445341757200025B42ACF317F8060854E0" +
        "0200007870000000C93081C60201003081A806072A8648CE38040130819C0241" +
        "00FCA682CE8E12CABA26EFCCF7110E526DB078B05EDECBCD1EB4A208F3AE1617" +
        "AE01F35B91A47E6DF63413C5E12ED0899BCD132ACD50D99151BDC43EE737592E" +
        "17021500962EDDCC369CBA8EBB260EE6B6A126D9346E38C50240678471B27A9C" +
        "F44EE91A49C5147DB1A9AAF244F05A434D6486931D2D14271B9E35030B71FD73" +
        "DA179069B32E2935630E1C2062354D0DA20A6C416E50BE794CA404160214595F" +
        "F421F82173A80C0D72991335A3131C9A065D740006504B435323387E7200196A" +
        "6176612E73656375726974792E4B657952657024547970650000000000000000" +
        "1200007872000E6A6176612E6C616E672E456E756D0000000000000000120000" +
        "787074000750524956415445";
}
