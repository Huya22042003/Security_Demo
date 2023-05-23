import { Button, Col, Form, Input, Row } from "antd";
import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { styled } from "styled-components";
import apiClient from "../services/apiClient";
import Cookies from "js-cookie";

const Register = () => {
  const [username, setUsername] = useState<string>();
  const [password, setPassword] = useState<string>();
  const navigate = useNavigate();

  const handleClickRegister = () => {
    try {
      apiClient
        .post("/api/register", {
          username,
          password,
        })
        .then((res: any) => {
          Cookies.set("auth", res.token);
          Cookies.set("refresh", res.refreshToken);
          if (res.status === 200) {
            navigate("/login");
          }
        });
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <Container>
      <h2>Create Your Account</h2>

      <Row justify="center">
        <Col span="18">
          <Form
            name="basic"
            autoComplete="false"
            onFinish={handleClickRegister}
          >
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
                Register
              </Button>
            </Form.Item>
          </Form>
        </Col>
      </Row>
      <p>
        Already have an account? <NavLink to="/login">Log In Now</NavLink>
      </p>
    </Container>
  );
};

export default Register;

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
