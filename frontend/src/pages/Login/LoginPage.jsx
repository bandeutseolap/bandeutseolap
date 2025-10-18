import React, { useState } from 'react';
import axios from "axios";

const LoginPage = () => {
    const [name, setName] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post('/member/login', { name }); // JSON 전송
            console.log(res.data);
            // 성공 처리...
        } catch (err) {
            console.error(err);
            // 에러 처리...
        }
    };

    return (
        <form onSubmit={handleSubmit} style={{ paddingTop: '10px' }}>
            <span>이름2</span>
            <input
                type="text"
                className="inputField"
                name="name"
                placeholder="이름을 입력하세요"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <button type="submit">등록</button>
        </form>
    );
};

export default LoginPage;
