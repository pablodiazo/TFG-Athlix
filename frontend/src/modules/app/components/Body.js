import React from "react";

import { Route, Routes } from "react-router-dom";

import Home from "./Home";
import Test from "./Test";

import { Login, Profile, SignUp, ChangePassword, UpdateProfile } from "../../users";
import { DailyPlan, CreateTrainingSession, CreateSessionSuccess } from "../../plans";

const Body = () => {
  return (
    <Routes>
      <Route path="/">
        <Route index exact element={<Home />} />
        <Route path="/test" element={<Test />} />
        <Route path="/users/login" element={<Login />} />
        <Route path="/users/profile" element={<Profile />} />
        <Route path="/users/signup" element={<SignUp />} />
        <Route path="/users/changePassword" element={<ChangePassword/>}/>
        <Route path="/users/updateProfile" element={<UpdateProfile/>}/>
        <Route path="/plans/daily" element={<DailyPlan />} />
        <Route path="/plans/create-training-session" element={<CreateTrainingSession />} />
        <Route path="/plans/create-session-success" element={<CreateSessionSuccess />} />
      </Route>
    </Routes>
  );
};

export default Body;
