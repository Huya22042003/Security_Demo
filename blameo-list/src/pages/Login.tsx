import { Button, Col, Form, Input, Row } from "antd";
import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import styled from "styled-components";
import Cookies from "js-cookie";
import apiClient from "../services/apiClient";

const Login = () => {
  const [username, setUsername] = useState<string>();
  const [password, setPassword] = useState<string>();
  const navigate = useNavigate();

  const handleLogin = async (values: any) => {
    try {
      const res: any = await apiClient.post("/api/login", values);
      console.log(res);
      Cookies.set("auth", res.data.accessToken);
      Cookies.set("refresh", res.data.refreshToken);
      if (res.status === 200) {
        navigate("/list");
      }
    } catch (error) {
      console.log("error: ", error);
    }
  };

  return (
    <Container>
      <h2>Log In</h2>
      <Row justify="center">
        <Col span={18}>
          <Form name="basic" autoComplete="false" onFinish={handleLogin}>
            <Form.Item
              label="Username"
              name="username"
              rules={[
                { required: true, message: "Please input your username!" },
              ]}
            >
              <Input
                name="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </Form.Item>
            <Form.Item
              label="Password"
              name="password"
              rules={[
                { required: true, message: "Please input your password!" },
              ]}
            >
              <Input.Password
                name="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </Form.Item>
            <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
              <Button type="primary" htmlType="submit">
                Login
              </Button>
            </Form.Item>
          </Form>
        </Col>
      </Row>
      <p>
        New User? <NavLink to="/register">Create Account</NavLink>
      </p>
    </Container>
  );
};

export default Login;

const Container = styled.div`
  text-align: center;
  border: 1px solid #f1f1f1;
  box-shadow: 2px 3px 16px 5px rgba(194, 194, 194, 0.74);
  -webkit-box-shadow: 2px 3px 16px 5px rgba(194, 194, 194, 0.74);
  -moz-box-shadow: 2px 3px 16px 5px rgba(194, 194, 194, 0.74);
  width: 500px;
  height: 300px;
  padding: 15px;

  @media screen and (max-width: 768px) {
    width: 300px;
    height: 300px;
  }
`;
