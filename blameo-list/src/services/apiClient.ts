import axios, { AxiosResponse } from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";

const apiClient = axios.create();
let isRefresh = false;

(apiClient.defaults.baseURL = import.meta.env.VITE_SERVER_APP),
  // Add a request interceptor
  apiClient.interceptors.request.use(
    (config: any) => {
      const token = Cookies.get("auth");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      config.headers["Content-Type"] = "application/json";
      return config;
    },
    function (err) {
      return Promise.reject(err);
    }
  );

// Add a response interceptor
apiClient.interceptors.response.use(
  function (response: AxiosResponse) {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    return response.data;
  },
  async (err) => {
    const originalConfig = err.config;

    // Access Token was expired
    if (err?.response?.status === 403 && !originalConfig._retry && !isRefresh) {
      if (Cookies.get("auth")) {
        originalConfig._retry = true;
        isRefresh = true;

        const config = {
          headers: {
            Authorization: `s${Cookies.get("refresh")}`,
          },
        };
        return apiClient
          .post("/api/refresh-token", config)
          .then((res: any) => {

            Cookies.set("auth", res.data);

            apiClient.defaults.headers.common[
              "Authorization"
            ] = `Bearer ${res.data}`;

            return apiClient(originalConfig);
          })
          .catch((err) => {
            console.log(err)
            Cookies.remove("auth");
            Cookies.remove("refresh");
            const navigate = useNavigate();
            navigate("/login");
          })
          .finally(() => {
            isRefresh = false;
          });
      } else {
        window.location.pathname = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default apiClient;
