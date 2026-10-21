<template>
  <div class="stack">
    <section class="card">
      <div class="card-head"><span class="card-title">개인정보 수집 동의</span></div>
      <dl class="info-grid">
        <div><dt>동의 여부</dt><dd><span class="chip" :class="user.consent.agreed ? 'chip-success' : 'badge-muted'">{{ user.consent.agreed ? '동의' : '미동의' }}</span></dd></div>
        <div><dt>동의일시</dt><dd>{{ user.consent.agreedAt || '-' }}</dd></div>
      </dl>
    </section>
    <p class="notice-box">조회 전용입니다. 동의 여부는 <strong>본인만 변경</strong>할 수 있으며, 관리자가 대신 동의할 수 없습니다.</p>
  </div>
</template>

<script>
export default { name: 'UserConsentTab', props: { user: { type: Object, required: true } } }
</script>
