package com.example.data

import com.example.model.PacketEntry
import com.example.model.PacketProtocol
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class PacketTelemetryEngine {

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private var packetCounter = 1000L

    private val packetTemplates = listOf(
        PacketTemplate(
            protocol = PacketProtocol.TLS,
            srcIp = "192.168.1.105",
            srcPort = 54210,
            dstIp = "142.250.190.46",
            dstPort = 443,
            length = 1420,
            flags = "ACK, PSH",
            ttl = 64,
            info = "TLSv1.3 Application Data [Encrypted Session]",
            hex = "17 03 03 05 8c a1 b4 c2 99 8a 3f 2e 10 99 a3 ff 44 21 89 dc eb 01 23 45",
            ascii = "........?..?.D!...#E",
            isSuspicious = false,
            anomaly = null
        ),
        PacketTemplate(
            protocol = PacketProtocol.DNS,
            srcIp = "192.168.1.105",
            srcPort = 58912,
            dstIp = "1.1.1.1",
            dstPort = 53,
            length = 74,
            flags = "RD",
            ttl = 64,
            info = "Standard query 0x8a21 A api.security-telemetry.io",
            hex = "8a 21 01 00 00 01 00 00 00 00 00 00 03 61 70 69 0c 73 65 63 75 72 69 74 79",
            ascii = ".!...........api.security",
            isSuspicious = false,
            anomaly = null
        ),
        PacketTemplate(
            protocol = PacketProtocol.HTTP,
            srcIp = "192.168.1.42",
            srcPort = 41200,
            dstIp = "192.168.1.1",
            dstPort = 80,
            length = 312,
            flags = "ACK, PSH",
            ttl = 60,
            info = "GET /api/v1/status HTTP/1.1 (Unencrypted Cleartext)",
            hex = "47 45 54 20 2f 61 70 69 2f 76 31 2f 73 74 61 74 75 73 20 48 54 54 50 2f 31",
            ascii = "GET /api/v1/status HTTP/1",
            isSuspicious = true,
            anomaly = "Plaintext HTTP payload containing IoT telemetry transmitted on local subnet."
        ),
        PacketTemplate(
            protocol = PacketProtocol.TCP,
            srcIp = "192.168.1.24",
            srcPort = 51240,
            dstIp = "192.168.1.180",
            dstPort = 22,
            length = 98,
            flags = "ACK",
            ttl = 64,
            info = "SSH-2.0-OpenSSH_9.6 encrypted packet exchange",
            hex = "53 53 48 2d 32 2e 30 2d 4f 70 65 6e 53 53 48 5f 39 2e 36 0d 0a 00 00 48",
            ascii = "SSH-2.0-OpenSSH_9.6...H",
            isSuspicious = false,
            anomaly = null
        ),
        PacketTemplate(
            protocol = PacketProtocol.ICMP,
            srcIp = "192.168.1.105",
            srcPort = 0,
            dstIp = "192.168.1.1",
            dstPort = 0,
            length = 64,
            flags = "Echo (ping) request",
            ttl = 64,
            info = "Echo (ping) request id=0x19f2, seq=4, ttl=64",
            hex = "08 00 4d 5b 19 f2 00 04 66 b1 89 2a 00 00 00 00 10 11 12 13 14 15 16 17",
            ascii = "..M[....f..*............",
            isSuspicious = false,
            anomaly = null
        ),
        PacketTemplate(
            protocol = PacketProtocol.UDP,
            srcIp = "192.168.1.215",
            srcPort = 5353,
            dstIp = "224.0.0.251",
            dstPort = 5353,
            length = 156,
            flags = "Multicast",
            ttl = 255,
            info = "mDNS PTR _googlecast._tcp.local query",
            hex = "00 00 00 00 00 01 00 00 00 00 00 00 0b 5f 67 6f 6f 67 6c 65 63 61 73 74",
            ascii = "............._googlecast",
            isSuspicious = false,
            anomaly = null
        ),
        PacketTemplate(
            protocol = PacketProtocol.ARP,
            srcIp = "192.168.1.1",
            srcPort = 0,
            dstIp = "192.168.1.105",
            dstPort = 0,
            length = 42,
            flags = "ARP Reply",
            ttl = 1,
            info = "Who has 192.168.1.105? Tell 192.168.1.1",
            hex = "00 01 08 00 06 04 00 01 f4 f5 e8 a1 3b 90 c0 a8 01 01 00 00 00 00 00 00",
            ascii = "............;...........",
            isSuspicious = false,
            anomaly = null
        ),
        PacketTemplate(
            protocol = PacketProtocol.DNS,
            srcIp = "192.168.1.42",
            srcPort = 49152,
            dstIp = "8.8.8.8",
            dstPort = 53,
            length = 88,
            flags = "RD",
            ttl = 60,
            info = "Standard query 0xfa12 A update-check.iot-cloud-firmware.xyz",
            hex = "fa 12 01 00 00 01 00 00 00 00 00 00 0c 75 70 64 61 74 65 2d 63 68 65 63 6b",
            ascii = ".............update-check",
            isSuspicious = true,
            anomaly = "Suspicious non-standard dynamic DNS TLD query (.xyz) from IoT node."
        )
    )

    fun startLiveCaptureFlow(intervalMs: Long = 850L): Flow<PacketEntry> = flow {
        while (true) {
            val template = packetTemplates[Random.nextInt(packetTemplates.size)]
            packetCounter++
            val timestamp = timeFormat.format(Date())
            
            val packet = PacketEntry(
                id = packetCounter,
                timestamp = timestamp,
                protocol = template.protocol,
                sourceIp = template.srcIp,
                sourcePort = template.srcPort,
                destIp = template.dstIp,
                destPort = template.dstPort,
                lengthBytes = template.length + Random.nextInt(10) - 5,
                flags = template.flags,
                ttl = template.ttl,
                info = template.info,
                payloadHex = template.hex,
                payloadAscii = template.ascii,
                isSuspicious = template.isSuspicious,
                suspiciousReason = template.anomaly
            )
            emit(packet)
            delay(intervalMs + Random.nextLong(200))
        }
    }

    fun getInitialPackets(count: Int = 15): List<PacketEntry> {
        val list = mutableListOf<PacketEntry>()
        val baseTime = System.currentTimeMillis() - (count * 900L)
        for (i in 0 until count) {
            val template = packetTemplates[i % packetTemplates.size]
            val timestamp = timeFormat.format(Date(baseTime + (i * 900L)))
            list.add(
                PacketEntry(
                    id = 1000L + i,
                    timestamp = timestamp,
                    protocol = template.protocol,
                    sourceIp = template.srcIp,
                    sourcePort = template.srcPort,
                    destIp = template.dstIp,
                    destPort = template.dstPort,
                    lengthBytes = template.length + (i * 3 % 20),
                    flags = template.flags,
                    ttl = template.ttl,
                    info = template.info,
                    payloadHex = template.hex,
                    payloadAscii = template.ascii,
                    isSuspicious = template.isSuspicious,
                    suspiciousReason = template.anomaly
                )
            )
        }
        return list
    }

    private data class PacketTemplate(
        val protocol: PacketProtocol,
        val srcIp: String,
        val srcPort: Int,
        val dstIp: String,
        val dstPort: Int,
        val length: Int,
        val flags: String,
        val ttl: Int,
        val info: String,
        val hex: String,
        val ascii: String,
        val isSuspicious: Boolean,
        val anomaly: String?
    )
}
