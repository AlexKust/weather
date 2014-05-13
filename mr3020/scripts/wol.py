#!/usr/bin/env python
# wol.py

import socket
import struct
import logging
import logging.handlers

sysLogger = None 

def wake_on_lan(macaddress):
    """ Switches on remote computers using WOL. """
    
    # Check macaddress format and try to compensate.
    if len(macaddress) == 12:
        pass
    elif len(macaddress) == 12 + 5:
        sep = macaddress[2]
        macaddress = macaddress.replace(sep, '')
    else:
        raise ValueError('Incorrect MAC address format')
                                                        
    # Pad the synchronization stream.
    data = ''.join(['FFFFFFFFFFFF', macaddress * 20])
    send_data = '' 

    # Split up the hex values and pack.
    for i in range(0, len(data), 2):
        send_data = ''.join([send_data,
            struct.pack('B', int(data[i: i + 2], 16))])
              
    # Broadcast it to the LAN.
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    sock.sendto(send_data, ('<broadcast>', 7))


if __name__ == '__main__':
    sysLogger = logging.getLogger('WoL')
    sysLogger.setLevel(logging.DEBUG)
    handler = logging.handlers.SysLogHandler(address = '/dev/log')
    formatter = logging.Formatter('%(name)s: %(message)s')
    handler.setFormatter(formatter)
    sysLogger.addHandler(handler)
    
    mac = 'B4:B5:2F:88:12:EB'
    sysLogger.info('Wake ' + mac)
    wake_on_lan(mac)
    sysLogger.info('Done')
