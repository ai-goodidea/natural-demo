import { baseRequestClient, requestClient } from '#/api/request';

export namespace AuthApi {
  export interface LoginParams {
    password?: string;
    username?: string;
  }

  export interface UserInfoVO {
    userId: string;
    username: string;
    realName: string;
    avatar: string;
    desc: string;
    homePath: string;
    roles: string[];
    token: string;
  }

  export interface LoginResult {
    accessToken: string;
    userInfo: UserInfoVO;
  }

  export interface RefreshTokenResult {
    data: string;
    status: number;
  }
}

/** 登录 */
export async function loginApi(data: AuthApi.LoginParams) {
  return requestClient.post<AuthApi.LoginResult>('/user/auth/login', data);
}

/** 退出登录 */
export async function logoutApi() {
  return baseRequestClient.post('/user/auth/logout', undefined, {
    withCredentials: true,
  });
}

/** 刷新 token：当前 demo 不支持，留接口以兼容 vben 框架 */
export async function refreshTokenApi() {
  return baseRequestClient.post<AuthApi.RefreshTokenResult>(
    '/user/auth/refresh',
    { withCredentials: true },
  );
}

/** 权限码 */
export async function getAccessCodesApi() {
  return requestClient.get<string[]>('/user/auth/codes');
}
