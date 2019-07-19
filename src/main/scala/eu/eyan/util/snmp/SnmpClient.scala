package eu.eyan.util.snmp

import org.snmp4j.smi.OID
import eu.eyan.log.Log
import org.snmp4j.TransportMapping
import org.snmp4j.smi.UdpAddress
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.snmp4j.Snmp
import org.snmp4j.PDU
import org.snmp4j.smi.VariableBinding
import org.snmp4j.smi.GenericAddress
import org.snmp4j.CommunityTarget
import org.snmp4j.smi.OctetString
import org.snmp4j.mp.SnmpConstants
import eu.eyan.util.scala.TryCatch
import eu.eyan.util.scala.Try
import java.io.Closeable
import eu.eyan.util.io.CloseablePlus
import eu.eyan.util.io.CloseablePlus.CloseablePlusImplicit
import org.snmp4j.event.ResponseEvent

object SnmpClient {
  def apply(host: String, port: Int, community: String) = new SnmpClient(host, port, community)
}

class SnmpClient(host: String, port: Int, community: String) extends Closeable {
  private val udpLink = s"udp:$host/$port"
  private val address = GenericAddress.parse(udpLink)
  private val transport = new DefaultUdpTransportMapping()
  private val snmp = new Snmp(transport)
  transport.listen

  private def target = {
    if (address == null) throw new WrongAddress(udpLink)
    val target = new CommunityTarget()
    target.setCommunity(new OctetString(community))
    target.setAddress(address)
    target.setRetries(2)
    target.setTimeout(1500)
    target.setVersion(SnmpConstants.version2c)
    Log.info(target)
    target
  }

  def close = Try { snmp.close }

  def getOid(oid: String) = getOids(List(oid))

  def getOids(oids: List[String]) = {
    Log.info(oids)
    val os = oids.map(new OID(_))
    val resp = getSnmpResponse(os)
    val result = os.zipWithIndex.map(x => getAsString(resp, x._2))
    Log.info("Result: " + result)
    result
  }

  private def getAsString(response: scala.util.Try[ResponseEvent], index: Int = 0) =
    response.map(r => r.getResponse().get(index).getVariable().toString())

  private def getSnmpResponse(oids: List[OID]) = {
    Try {
      val pdu = new PDU()
      for (oid <- oids) pdu.add(new VariableBinding(oid))
      Log.info("pdu: " + pdu)

      val event = snmp.send(pdu, target, null)

      if (event != null) event
      else throw new SnmpTimeout
    }
  }

  class SnmpTimeout extends Exception("Timeout")
  class WrongAddress(address: String) extends Exception("Wrong address: " + address)
}
