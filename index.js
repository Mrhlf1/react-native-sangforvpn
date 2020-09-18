import React, {Component} from 'react';
import PropTypes from 'prop-types';

import {
    NativeModules,
} from 'react-native';

const vpn = NativeModules.RNSangforVpn;
export default vpn;
export const Mode = {
  EasyApp: 0,
  L3VPN: 1,
}
