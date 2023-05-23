import Cookies from "js-cookie";

export const getLocalAccessToken = () => {
  const accessToken = Cookies.get("auth");
  return accessToken;
};

export const getLocalRefreshToken = () => {
  const token = Cookies.get("refresh");
  return token;
};

export const updateLocalAccessToken = (token: string) => {
  Cookies.set("auth", token);
};
