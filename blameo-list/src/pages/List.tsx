import { Button, notification } from "antd";

import { useEffect, useState } from "react";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import { styled } from "styled-components";
import apiClient from "../services/apiClient";

interface IVoucher {
  voucherId: number;
  code: string;
  persen: number;
}

function parseJwt(token: any) {
  const base64Url = token.split(".")[1];
  const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  const jsonPayload = decodeURIComponent(
    window
      .atob(base64)
      .split("")
      .map(function (c) {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join("")
  );

  return JSON.parse(jsonPayload);
}

const List = () => {
  const navigate = useNavigate();
  const [userName, setUserName] = useState<string>("");
  const [listEmployee, setListEmployee] = useState<IVoucher[]>();
  const auth = Cookies.get("auth");

  const decodeJwt = parseJwt(auth);

  const fetchData = async () => {
    try {
      const config = {
        headers: {
          Authorization: `Bearer ${auth}`,
        },
      };
      const response: any = await apiClient.get("/api/employee", config);
      setListEmployee(response.data);
      return response.data;
    } catch (error) {
      navigate("/login");

      throw new Error("Login failed");
    }
  };
  useEffect(() => {
    fetchData();
    setUserName(decodeJwt.sub);
  }, []);

  const handleUpdate = () => {
    apiClient
      .put(`/api/update-name`, { userName: userName })
      .then(() => {
        notification.success({
          message: "Thong bao",
          description: "Cap nhat thanh cong",
        });
        navigate("/list");
      })
      .catch(() => {
        notification.error({
          message: "Thong bao",
          description: "Cap nhat that bai",
        });
      });
  };

  const handleLogout = () => {
    Cookies.remove("auth");
    Cookies.remove("refresh");
    navigate("/login");
  };
  return (
    <>
      <h2>List User</h2>
      <Header>
        <div>
          <input
            type="text"
            name="userName"
            value={userName}
            onChange={(e: any) => setUserName(e.target.value)}
          />
          <button className="btn btn-primary" onClick={handleUpdate}>
            Update
          </button>
        </div>
        <Button onClick={handleLogout}>Logout</Button>
      </Header>
      <table className="table">
        <thead>
          <tr>
            <th scope="col">Id</th>
            <th scope="col">Name</th>
            <th scope="col">Gender</th>
            <th scope="col">DOB</th>
            <th scope="col">Address</th>
            <th scope="col">Department</th>
          </tr>
        </thead>
        <tbody>
          {listEmployee &&
            listEmployee.map((employee: any) => {
              return (
                <tr key={employee.id}>
                  <td>{employee.id}</td>
                  <td>{employee.name}</td>
                  <td>{employee.gender}</td>
                  <td>{employee.dob}</td>
                  <td>{employee.address}</td>
                  <td>{employee.department}</td>
                </tr>
              );
            })}
        </tbody>
      </table>
    </>
  );
};

export default List;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;
