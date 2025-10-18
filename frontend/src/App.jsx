// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from "./pages/Login/LoginPage";
import MemberListPage from "./pages/MemberList/MemberListPage";

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/member/login" element={<LoginPage />} />
            </Routes>
            <Routes>
                <Route path="/members" element={<MemberListPage />} />
            </Routes>
        </BrowserRouter>
    );
}
