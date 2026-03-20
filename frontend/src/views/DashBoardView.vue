<template>
  <div class="page-content">
    <div class="page-header">
      <h1 class="section-title">Dashboard</h1>
      <p class="section-desc">대시보드 화면입니다.</p>
    </div>

    <div class="grid-12">
      <section class="col-6 panel">
        <div class="panel-body">6 Grid</div>
      </section>

      <section class="col-6 panel">
        <div class="panel-body">6 Grid</div>
      </section>

      <section class="col-4 panel">
        <div class="panel-body">4 Grid</div>
      </section>

      <section class="col-4 panel">
        <div class="panel-body">4 Grid</div>
      </section>

      <section class="col-4 panel">
        <div class="panel-body">4 Grid</div>
      </section>

      <section class="col-2 panel">
        <div class="panel-body">2 Grid</div>
      </section>

      <section class="col-2 panel">
        <div class="panel-body">2 Grid</div>
      </section>

      <section class="col-2 panel">
        <div class="panel-body">2 Grid</div>
      </section>

      <section class="col-2 panel">
        <div class="panel-body">2 Grid</div>
      </section>

      <section class="col-2 panel">
        <div class="panel-body">2 Grid</div>
      </section>

      <section class="col-2 panel">
        <div class="panel-body">2 Grid</div>
      </section>
    </div>

    <div class="mt-6 flex gap-4">
      <button class="btn btn-common">Common</button>
      <button class="btn btn-success">Success</button>
      <button class="btn btn-warn">Warn</button>
      <button class="btn btn-error">Error</button>
      <button class="btn btn-info">Info</button>
      <button class="btn btn-secondary">Secondary</button>
      <button class="btn btn-outline-common">Outline</button>
      <button class="btn btn-ghost">Ghost</button>
    </div>

    <div class="grid-12">
      <section class="col-6 panel">
        <div class="panel-body">Dashboard Card A</div>
      </section>

      <section class="col-6 panel">
        <div class="panel-body">Dashboard Card B</div>
      </section>
      <section class="col-12 panel">
        <div class="panel-body">
          <button class="btn btn-common">확인</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DashboardView'
}
</script>