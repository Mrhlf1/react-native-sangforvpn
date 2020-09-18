//
//  RNSangforVpn.m
//  RNSangforVpn
//
//  Created by Johnny iDay on 2017/11/30.
//  Copyright © 2017年 Johnny iDay. All rights reserved.
//

#import "RNSangforVpn.h"




@interface RNSangforVpn()
{
    VPNMode _sdkMode;
    NSURL *_url;
    NSString *_smsCode;
    RCTPromiseResolveBlock _resolve;
    RCTPromiseRejectBlock _reject;
}
@end

@implementation RNSangforVpn

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

/*!
认证失败回调

@param error 错误信息
*/
- (void)onLoginFailed:(NSError *)error
{
    _reject([NSString stringWithFormat:@"%ld", error.code], error.description, error);
}

/*!
 认证过程回调
 nextAuthType为VPNAuthTypeSms、VPNAuthTypeRadius、VPNAuthTypeForceUpdatePwd
 这三个类型的中的一个时，msg才不为空，具体的类参考上面对信息类的定义
 
 @param nextAuthType 下个认证类型
 @param msg 认证需要的信息类
 */
- (void)onLoginProcess:(VPNAuthType)nextAuthType message:(BaseMessage *)msg
{
    if (nextAuthType != VPNAuthTypeNone) {
        _resolve(@{@"success": [NSString stringWithFormat:@"%lu", nextAuthType]});
    }
}

/*!
 认证成功回调
 */
- (void)onLoginSuccess
{
    _resolve(@{@"success": @"1"});
}

- (NSDictionary *)constantsToExport
{
    return @{@"L3VPN" : @(VPNModeL3VPN)};
};

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(init, host:(NSString *)host port:(int)port resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    _resolve = resolve;
    _reject = reject;
    _sdkMode = VPNModeL3VPN;
    _url = [NSURL URLWithString:[NSString stringWithFormat:@"%@:%d", host, port]];
    self.helper = SangforAuthManager.getInstance;
    self.helper.delegate = self;
//    _resolve(@{@"success": @"1"});
}

RCT_REMAP_METHOD(init, mode:(VPNMode)mode host:(NSString *)host port:(int)port resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    _resolve = resolve;
    _reject = reject;
    _sdkMode = mode;
    _url = [NSURL URLWithString:[NSString stringWithFormat:@"%@:%d", host, port]];
    self.helper = SangforAuthManager.getInstance;
    self.helper.delegate = self;
//    _resolve(@{@"success": @"1"});
}

RCT_REMAP_METHOD(login, username:(NSString *)username password:(NSString *)password resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    _resolve = resolve;
    _reject = reject;
    [self.helper startPasswordAuthLogin:_sdkMode vpnAddress:_url username:username password:password];
    
}

RCT_REMAP_METHOD(logout, resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    _resolve = resolve;
    _reject = reject;
    [self.helper vpnLogout];
}

@end
