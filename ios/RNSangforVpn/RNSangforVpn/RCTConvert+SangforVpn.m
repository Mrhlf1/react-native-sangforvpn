//
//  RCTConvert+SangforVpn.m
//  RNSangforVpn
//
//  Created by Johnny iDay on 2017/12/18.
//  Copyright © 2017年 Johnny iDay. All rights reserved.
//

#import "RCTConvert+SangforVpn.h"
#import "SangforAuthHeader.h"

@implementation RCTConvert (SangforVpn)

    RCT_ENUM_CONVERTER(VPNMode, (@{@"L3VPN" : @(VPNModeL3VPN)}),
                       VPNModeL3VPN, integerValue);

@end
